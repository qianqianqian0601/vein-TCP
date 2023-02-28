package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.util.QuaternionToEulerAngle;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.*;
import static java.lang.Math.PI;

public class BVHMClass {


    private static StringBuffer bvhBuf;//全局buffer字符缓存
    private static StringBuffer eulerBuf;//全局欧拉角数据缓存
    private static List<String> bonesList = new ArrayList<>();//全局buffer字符缓存
    private int count=0;//num缩进计数用,count解析计数用
    private SocketIOServer server;
    public BVHMClass(SocketIOServer server) {
        this.server = server;
    }


    public void writeBVH(JSONObject jsonObject) throws IOException {
        String readFilePath = jsonObject.getString("filePath");//文件路径
        String readFileName = jsonObject.getString("fileName");//文件名称
        Integer startFrame = jsonObject.getInteger("startFrame");//开始帧
        Integer endFrame = jsonObject.getInteger("endFrame");//结束帧
        String bvhFilePath = jsonObject.getString("newFilePath");//bvh保存路径
        String bvhFileName = jsonObject.getString("newFileName");//bvh名称
        String order = jsonObject.getString("order");//bvh名称
        System.out.println("--------"+readFilePath);
        System.out.println("--------"+readFileName);
        System.out.println("--------"+startFrame);
        System.out.println("--------"+endFrame);
        System.out.println("--------"+bvhFilePath);
        System.out.println("--------"+bvhFileName);
        bvhBuf = new StringBuffer();
        eulerBuf = new StringBuffer();
        bvhBuf.append("HIERARCHY\n");//在bvhBuf缓存中写文件头
        String deviceOneLine = readDeviceOneLine(readFilePath, readFileName);//设备列表第一行信息
        JSONObject deviceInfo = JSONObject.parseObject(deviceOneLine);//转换Json
        String veinList = deviceInfo.getJSONObject("veinList").getJSONObject("bvhBones").toJSONString();
       /* JSONArray array = JSONArray.parseArray(veinList);
        JSONObject bones = array.getJSONObject(0);*/
        jsonJX(veinList);//解析JSON并将解析骨骼格式列表放在bvhBuf缓存中
        bvhBuf.append("MOTION\n");
        int Frames = endFrame - startFrame + 1;
        bvhBuf.append("Frames:  "+Frames+"\n");
        DecimalFormat decimalFormat = new DecimalFormat("#0.00000000");
        String framesTime = decimalFormat.format((50.00 / 1000.00));
        bvhBuf.append("Frame Time: " + framesTime);
        //readActionData(readFilePath, readFileName, startFrame, endFrame,order);//读取动作数据
        readActionData(readFilePath, readFileName, startFrame, endFrame,"XYZ");//读取动作数据
        File file = new File(bvhFilePath + bvhFileName + ".bvh");//bvh文件
        file.createNewFile();//创建新文件
        FileWriter writer = new FileWriter(file);//向文件进行写操作
        writer.write(bvhBuf.toString());//写BVH骨骼
        writer.write("\n");//换行
        writer.write(eulerBuf.toString());//写欧拉角数据
        writer.flush();//刷新写流
        writer.close();//关闭写流
        JSONObject msgInfo = new JSONObject();
        msgInfo.put("control","BVHControl");
        msgInfo.put("success",1);
        msgInfo.put("info","BVH文件生成完成");
        server.getBroadcastOperations().sendEvent("msgInfo",msgInfo);
    }

    //读取设备列表文件第一行，获取骨骼顺序信息
    private static String readDeviceOneLine(String readFilePath, String readFileName) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(readFilePath).append(readFileName).append(".di");
        File deviceFile = new File(sb.toString());
        FileReader fileReader = new FileReader(deviceFile);
        LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
        String info;
        while ((info=lineNumberReader.readLine())!=null){
            if (lineNumberReader.getLineNumber()==1){
                break;
            }
        }
        return info;
    }

    /**
     * 读取动作数据文件
     * @param readFilePath 动作数据文件路径
     * @param readFileName 动作数据文件名称
     * @param startFrame 从第几帧开始读取
     * @param endTime    读取到第几帧结束
     * @param order
     * @throws IOException
     */
    private static void readActionData(String readFilePath, String readFileName, int startFrame, int endTime, String order) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(readFilePath).append(readFileName).append(".txt");//拼接动作数据文件路径
        File actionDataFile = new File(sb.toString());//获取动作数据文件
        FileReader fileReader = new FileReader(actionDataFile);//创建文件读取流
        LineNumberReader lineNumberReader = new LineNumberReader(fileReader);//按行读取函数
        for (int i = startFrame; i <= endTime; i++) {//循环读取指定行数的数据
            readActionDataToLine(bonesList,lineNumberReader, i,order);
        }
    }

    /**
     * 获取指定行数的动作数据并转欧拉角
     * @param bonesList 骨骼顺序列表
     * @param lineNumberReader  文件按行读取流
     * @param LineNumber 指定读取行数
     * @param order 转轴顺序
     * @throws IOException
     */
    private static void readActionDataToLine(List<String> bonesList, LineNumberReader lineNumberReader, int LineNumber, String order) throws IOException {
        String info;
        while ((info=lineNumberReader.readLine())!=null){//info接收读取到动作数据
            if (lineNumberReader.getLineNumber()==LineNumber){
                break;//读取到置顶行数就停止
            }
        }
        JSONObject json = JSONObject.parseObject(info);//转换json
        JSONArray coordinate = json.getJSONArray("coordinate");//获取对应骨骼的四元数
        Double coordinateZero = coordinate.getDouble(0);
        Double coordinateOne = coordinate.getDouble(1);
        Double coordinateTwo = coordinate.getDouble(2);
        eulerBuf.append(coordinateZero).append(" ").append(coordinateOne).append(" ").append(coordinateTwo).append(" ");
        for (String bone : bonesList) {//循环骨骼循序
            if (json.containsKey(bone)){
                JSONArray jsonArray = json.getJSONArray(bone);//获取对应骨骼的四元数
                Double w = jsonArray.getDouble(0);
                Double x = jsonArray.getDouble(1);
                Double y = jsonArray.getDouble(2);
                Double z = jsonArray.getDouble(3);
                double[] EA = QuaternionToEulerAngle.toEulerAngle(order,w, x, y, z);//调用四元数转换欧拉角函数
                double dataOne = EA[0] , dataTwo = EA[1] , dataThree = EA[2];
                eulerBuf.append(dataOne).append(" ").append(dataTwo).append(" ").append(dataThree).append(" ");
            }else {
                double dataOne = 0.0, dataTwo = 0.0 , dataThree = 0.0;
                eulerBuf.append(dataOne).append(" ").append(dataTwo).append(" ").append(dataThree).append(" ");
            }
        }
        eulerBuf.append("\n");
    }


    /**
     * 解析骨骼列表顺序
     * @param jsonString
     */
    public void jsonJX(String jsonString) {
        count++;
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            if (!key.equals("offset")){
                String cJson = next.getValue().toString();
                if(key.equals("endSite")){
                    bvhBuf.append(strPloidy(count)+"End Site"+"\n"+strPloidy(count)+"{\n");
                    JSONArray offset = jsonObject.getJSONObject("endSite").getJSONArray("offset");
                    bvhBuf.append(strPloidy(count+1)+"OFFSET "+strAddTab(offset)+"\n");
                }else if (!key.equals("endSite")&&count<=1){
                    bonesList.add(key);
                    bvhBuf.append("ROOT"+strPloidy(count)+key+"\n"+strPloidy(count)+"{\n");
                    JSONArray offset = jsonObject.getJSONObject(key).getJSONArray("offset");
                    bvhBuf.append(strPloidy(count+1)+"OFFSET "+strAddTab(offset)+"\n");
                    bvhBuf.append(strPloidy(count+1)+"CHANNELS 6 Xposition Yposition Zposition Xrotation Yrotation Zrotation\n");
                }else if (!key.equals("endSite")&&count>1){
                    bonesList.add(key);
                    bvhBuf.append(strPloidy(count)+"JOINT "+key+"\n"+strPloidy(count)+"{\n");
                    JSONArray offset = jsonObject.getJSONObject(key).getJSONArray("offset");
                    bvhBuf.append(strPloidy(count+1)+"OFFSET "+strAddTab(offset)+"\n");
                    bvhBuf.append(strPloidy(count+1)+"CHANNELS 3 Xrotation Yrotation Zrotation\n");
                }
                jsonJX(cJson);
                bvhBuf.append(strPloidy(count)+"}\n");
            }
        }
        count--;
    }


    //添加缩进方法,参数num:缩进次数
    private static String strPloidy(int num) {
        String str = "  ";
        for (int i = 1; i < num; i++) {
            str += "  ";
        }
        return str;
    }

    //解析offset偏移量数组方法，在每个元素中间添加空格
    private static String strAddTab(JSONArray json) {
        String str = "";
        for (int i = 0; i < json.size(); i++) {
            str += " " + json.get(i);
        }
        return str;
    }



}
