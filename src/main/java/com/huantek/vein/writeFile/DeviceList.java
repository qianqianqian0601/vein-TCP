package com.huantek.vein.writeFile;

import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.Service.ServiceImpl.RecordControl;
import com.huantek.vein.socket.SocketThreadFirm;
import com.huantek.vein.util.PublicVariable;
import lombok.SneakyThrows;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceList extends Thread {

    String url = "";
    String version = "";
    String veinList = "";
    private File deviceFile;
    public void params(JSONObject jsonObject) {
        url = jsonObject.getString("url");
        version = jsonObject.getString("version");
        veinList = jsonObject.getJSONObject("veinList").toJSONString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String startTime = sdf.format(new Date());
        deviceFile = new File(url+startTime+".di");
    }
    private long time = 1000;

    private void writeDeviceList() throws IOException {
        FileWriter writer;
        if (!deviceFile.exists()){
            writer = new FileWriter(deviceFile);
            deviceFile.createNewFile();
        }else {
            writer = new FileWriter(deviceFile,true);
        }
        writer.write(JSONObject.toJSONString(SocketThreadFirm.firmSuits));
        writer.write("\r\n");
        writer.flush();
        writer.close();
    }

    public void stopWrite() {
        String prefix = "temp";//前缀
        String suffix = ".di";//后缀
        FileInputStream inputStream;//文件输入流
        BufferedReader reader;//读取流
        FileWriter writer;//写文件流
        String text;//接收变量
        try {
            if (deviceFile.isFile() && deviceFile.exists()) {//判断文件是否存在
                inputStream = new FileInputStream(deviceFile);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                File tempFile = File.createTempFile(prefix, suffix, new File(url));//新建临时文件
                writer = new FileWriter(tempFile);//打开临时文件的写流·
                JSONObject obj = new JSONObject();
                int recordFrameCount = PublicVariable.recordFrameCount;//总帧率
                obj.put("sumFrame",recordFrameCount);
                obj.put("version",version);
                obj.put("veinList",veinList);
                obj.put("startTime", RecordControl.StartTime);
                obj.put("endTime",RecordControl.EndTime);
                writer.write(obj.toJSONString()+"\r\n");//写首行内容
                while ((text = reader.readLine()) != null) {//按行读取文件并接收
                    writer.write(text + "\r\n");//将读到的文件写入临时文件并换行
                }
                writer.flush();//刷新
                writer.close();//关闭写文件流
                reader.close();//关闭读文件流
                inputStream.close();//关闭输入流
                String name = deviceFile.getName();//获取文件名称
                String sub = name.substring(0, name.lastIndexOf("."));
                deviceFile.delete();//删除原文件
                if (tempFile.isFile() && tempFile.exists()) {//判断临时文件是否存在
                    String newName = this.url +sub+suffix;//文件新名称
                    File newFile = new File(newName);
                    tempFile.renameTo(newFile);//重命名文件
                }
            } else {
                System.out.println("文件不存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true){
            synchronized (deviceFile){
                if ("start".equals(PublicVariable.recordThread)){
                    writeDeviceList();
                    sleep(time);
                } else if ("stop".equals(PublicVariable.recordThread)){
                    stopWrite();
                    break;
                }
            }
        }
    }


}
