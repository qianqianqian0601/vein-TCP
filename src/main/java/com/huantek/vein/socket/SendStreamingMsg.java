package com.huantek.vein.socket;

import com.alibaba.fastjson.JSONObject;
import com.huantek.jni.conversionData.ConversionData;
import com.huantek.vein.util.QuaternionToEulerAngle;
import com.huantek.vein.util.PublicVariable;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SendStreamingMsg extends Thread {
    ServerSocket serverSocket;
    Socket socket;
    DatagramSocket datagramSocket;
    String order;
    OutputStream outputStream;
    Integer port;
    private Integer type = 0;
    static byte[] head = "??".getBytes();
    static byte[] tail = "!!".getBytes();
    public SendStreamingMsg(Socket socket, String order) {
        this.socket  = socket;
        this.order = order;
    }

    public SendStreamingMsg(DatagramSocket streamingDatagramSocket, String order, Integer type, Integer streamingPort) {
        this.datagramSocket  = streamingDatagramSocket;
        this.order = order;
        this.type = type;
        this.port = streamingPort;
    }


    @Override
    public void run() {
        try {
            while (PublicVariable.threadFlagTwo){
                if (type==0){
                        outputStream = socket.getOutputStream();
                        Set<Map.Entry<String, Object>> entries = PublicVariable.VeinData.entrySet();
                        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
                        JSONObject jsonObject = new JSONObject();
                        while (iterator.hasNext()) {
                            Map.Entry<String, Object> next = iterator.next();
                            String key = next.getKey();
                            if (!"coordinate".equals(key)
                                    && !"frame".equals(key)
                                    && !"control".equals(key)) {
                                Object[] objects = PublicVariable.VeinData.getJSONArray(key).toArray();
                                double w = (double) objects[0];
                                double x = (double) objects[1];
                                double y = (double) objects[2];
                                double z = (double) objects[3];
                                double[] EA = QuaternionToEulerAngle.toEulerAngle(order, w, x, y, z);
                                jsonObject.put(key, EA);
                            }
                            if ("coordinate".equals(key)){
                                Object[] objects = PublicVariable.VeinData.getJSONArray(key).toArray();
                                jsonObject.put(key, objects);
                            }
                        }
                    String s1 = String.valueOf(jsonObject);//json转string
                    byte[] sumData = s1.getBytes();//string转byte[]
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    stream.write(head, 0, head.length);
                    stream.write(sumData, 0, sumData.length);
                    stream.write(tail, 0, tail.length);
                    byte[] bytes = stream.toByteArray();
                    outputStream.write(bytes);
                    outputStream.flush();
                    SendStreamingMsg.sleep(ConversionData.time);
                }else if (type==1){
                    Set<Map.Entry<String, Object>> entries = PublicVariable.VeinData.entrySet();
                    Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
                    JSONObject jsonObject = new JSONObject();
                    while (iterator.hasNext()){
                        Map.Entry<String, Object> next = iterator.next();
                        String key = next.getKey();
                        if (!"coordinate".equals(key)
                                && !"frame".equals(key)
                                && !"control".equals(key)){
                            Object[] objects = PublicVariable.VeinData.getJSONArray(key).toArray();
                            double w = (double) objects[0];
                            double x = (double) objects[1];
                            double y = (double) objects[2];
                            double z = (double) objects[3];
                            double[] EA = QuaternionToEulerAngle.toEulerAngle(order, w, x, y, z);
                            jsonObject.put(key,EA);
                        }
                        if ("coordinate".equals(key)){
                            Object[] objects = PublicVariable.VeinData.getJSONArray(key).toArray();
                            jsonObject.put(key, objects);
                        }
                    }
                String s1 = String.valueOf(jsonObject);//json转string
                byte[] sumData = s1.getBytes();//string转byte[]
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                stream.write(head,0,head.length);
                stream.write(sumData,0,sumData.length);
                stream.write(tail,0,tail.length);
                byte[] info = stream.toByteArray();
                DatagramPacket datagramPacket = new DatagramPacket(info,info.length,InetAddress.getByName("255.255.255.255"),port);
                datagramSocket.send(datagramPacket);
                SendStreamingMsg.sleep(ConversionData.time);
                }
            }
            if (type==0){
                if (!socket.isOutputShutdown()) socket.shutdownOutput();
                if (!socket.isInputShutdown()) socket.shutdownInput();
                if (!socket.isClosed()) socket.close();
                if (!serverSocket.isClosed()) serverSocket.close();
            }else {
                if (!datagramSocket.isClosed()) datagramSocket.isClosed();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
