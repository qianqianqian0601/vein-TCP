package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class TestSendAndReceive {
    private  ConcurrentHashMap<String,Socket> socketMap;
    private byte[] arr = {0x47,0x0B,0x01,0x00,0x74};//测试通信时长


    public TestSendAndReceive(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    public void testCommunicationService(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if (action==null||action.equals("")){
            System.out.println("参数未传入");
        }else {
            if ("testFirmwareSendAndReceive".equals(action)) testFirmwareSendAndReceive();//测试固件通信时长
        }
    }

    private void testFirmwareSendAndReceive() {
        try {
            if (!socketMap.isEmpty()){
                for (Socket socket : socketMap.values()) {
                    if (socket!=null && socket!=socketMap.get("Algorithm")){
                        socket.getOutputStream().write(arr);//发送测试通信命令
                        System.out.println("发送测试通信命令");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
