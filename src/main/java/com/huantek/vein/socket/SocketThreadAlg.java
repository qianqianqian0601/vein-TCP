package com.huantek.vein.socket;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.util.ParseDataAlg;
import com.huantek.vein.util.LostFrame;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class SocketThreadAlg extends Thread{
    //创建和本线程相关的Socket
    private HashMap<String,Socket> socketMap = null;
    private SocketIOServer server = null;
    private Vector<Byte> bytes = null;
    public static int receiveFrameCountALG = 0;

    public SocketThreadAlg(SocketIOServer server, HashMap<String, Socket> socketMap){
        this.socketMap = socketMap;
        this.server = server;
    }



    @Override
    public void run(){
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        OutputStream outputStream = null;
        PrintWriter printWriter = null;
        Socket socket = null;


        if (socketMap.get("Firmware")==null){
            System.out.println("算法连接成功,等待固件连接---");
        }else {
            System.out.println("连接就绪,等待接收固件信息...");
        }

        try {
            socket = socketMap.get("Algorithm");
            socket.setSoTimeout(2000);//设置1秒超时，一秒没有接受到数据就关闭和算法的socket连接
            if (socket!=null){
                inputStream = socket.getInputStream();//获取输入流读取客户端信息

                int info , s ;
                bytes = new Vector<>();//使用Vector动态存储Byte存储固件数据
                while ((info = inputStream.read())!=-1){//循环读，-1表示读完
                    s = info;
                    bytes.add((byte) s);//将数据存入Vector
                    List<Byte> pfd = parseAlgorithmData();
                    if (pfd!=null){
                        receiveFrameCountALG++;
                        ParseDataAlg parseDataAlg = new ParseDataAlg(pfd);
                        JSONObject object = parseDataAlg.returnParseData();
                        object.put("control","veinData");
                        server.getBroadcastOperations().sendEvent("msgInfo",object.toString());//将动作数据转发给前端
                    }
                }
                socket.shutdownInput();
            }

        } catch (Exception e) {
            System.out.println("socketThreadALG");
            e.printStackTrace();
        } finally {
            try {
                if (printWriter!=null) printWriter.close();
                if (outputStream!=null) outputStream.close();
                if (bufferedReader!=null) bufferedReader.close();
                if (inputStreamReader!=null) inputStreamReader.close();
                if (inputStream!=null) inputStream.close();
                if (socket!=null) {
                    socket.close();
                    socketMap.remove("Algorithm");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //接收算法数据
    public List<Byte> parseAlgorithmData() throws IOException {
        for (int i=0;i<bytes.size();i++){//循环Vector
            if (bytes.get(i)==0x47){//首先找到帧头
                if (bytes.size()<i+7){//避免在获取数据长度时数组越界
                    return null;
                }
                List<Byte> dataSizeByte = bytes.subList(2, 6);//获取数据长度4个字节
                int dataSize = bytesToIntBig(ListToByte(dataSizeByte), 0);
                int len = dataSize+7;
                if (bytes.size()-i >= len){//长度足够
                    Byte aByte = bytes.get(i+len-1);
                    if (aByte==0x74){//找帧尾
                        List<Byte> sub;
                        Vector<Byte> clone = (Vector<Byte>) bytes.clone();
                        sub = clone.subList(i,i+len);//将获取的完整报文放在新的容器中
                        bytes.subList(0,i+len).clear();//将已经获取到的报文删除，继续获取下一个报文
                        return sub;
                    }else {//继续循环找帧尾
                        Vector<Byte> errorData = (Vector<Byte>) bytes.clone();
                        List<Byte> errorDataList = errorData.subList(i, i + len + 1);
                        LostFrame.errorDataLogALG(errorDataList);
                        this.bytes.subList(0,i+len+1).clear();
                        continue;
                    }
                }else {//长度不够继续向Vector存数据
                    return null;
                }
            }else {//继续循环找帧头
                continue;
            }
        }
        return null;
    }



    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序-小端序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToIntSmall(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序-大端序。和intToBytes2（）配套使用
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }


    /**
     * list<byte>转Byte[]
     * @param list
     * @return
     */
    public static byte[] ListToByte(List<Byte> list){
        if (list == null || list.size()<0) return null;

        byte [] arr = new byte[list.size()];
        Iterator<Byte> iterator = list.iterator();
        int i = 0;
        while (iterator.hasNext()){
            arr[i] = iterator.next();
            i++;
        }
        return arr;
    }


}
