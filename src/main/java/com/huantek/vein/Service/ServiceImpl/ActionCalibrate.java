package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.jni.conversionData.ConversionData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ActionCalibrate {
    SocketIOServer server;
    ConcurrentHashMap<String, Socket> map;
    public ActionCalibrate(SocketIOServer server, ConcurrentHashMap<String, Socket> socketMap) {
        this.server = server;
        this.map = socketMap;
    }


    //Vein校准
    public void VeinCalibrate(JSONObject jsonObject) throws IOException {
        String status = jsonObject.getString("status");
        String pose = jsonObject.getString("pose");
        if ("T-pose".equals(pose)){
            ConversionData.pose = 0;
        }else if ("I-pose".equals(pose)){
            ConversionData.pose = 1;
        }else if ("S-pose".equals(pose)){
            ConversionData.pose = 2;
        }
        if (status.equals("start")) {
            ConversionData.cbFlag = false;
        }
        if (status.equals("stop")) {
            ConversionData.cbFlag = true;
            String finalMsg = pose + " calibration finish";
            sendU3DMsg(finalMsg);
        }
    }



    //校准完成向U3D发送完成信息
    static byte[] head = "??".getBytes();
    static byte[] tail = "!!".getBytes();
    public void sendU3DMsg(String finalMsg) throws IOException {
        if (map.containsKey("U3D")){
            Socket socket = map.get("U3D");
            byte[] finalMsgBytes = finalMsg.getBytes();
            OutputStream outputStream = socket.getOutputStream();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(head,0,head.length);
            stream.write(finalMsgBytes,0,finalMsgBytes.length);
            stream.write(tail,0,tail.length);
            byte[] bytes = stream.toByteArray();
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

}
