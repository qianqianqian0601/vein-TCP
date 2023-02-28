package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.huantek.jni.conversionData.ConversionData;
import com.huantek.vein.util.OrderBase;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class frameControl {
    ConcurrentHashMap<String,Socket> socketMap;
    public frameControl(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }


    public void FPS(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if (action.equals("settingFPS")){
            frameSetting(jsonObject);
        }else if (action.equals("queryFPS")){
            queryFPS(jsonObject);
        }
    }

    private void queryFPS(JSONObject jsonObject) {
        String productID = jsonObject.getString("productID");
        String mac = jsonObject.getString("mac");
        if (productID.equals("") || productID==null) System.out.println("未传入套装参数");
        else if (mac.equals("") || mac==null) System.out.println("未传入MAC参数");
        else {
            try {
                String socketName = productID + mac;
                Socket socket = socketMap.get(socketName);//获取对应节点socket
                if (socket!=null){
                    socket.getOutputStream().write(OrderBase.FPS_QUERY);//发送节点LED关闭指令
                    System.out.println("查询FPS");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public void frameSetting(JSONObject jsonObject) {
        Integer frame = jsonObject.getInteger("frame");
        if (frame==50){
            ConversionData.time = 18;
            try {
                if (!socketMap.isEmpty()){
                    int count = 0 ;
                    for (Socket socket : socketMap.values()) {
                        if (socket!=null && socket!=socketMap.get("Algorithm")){
                            count++;
                            System.out.println(count+"::::"+socket.getPort());
                            socket.getOutputStream().write(OrderBase.FPS_50);
                        }
                    }
                }
                System.out.println("设置50帧率");
            }catch (Exception e){
                System.out.println("设置帧率失败");
                e.printStackTrace();
                System.out.println("设置帧率失败");
            }
        }else if (frame == 100){
            ConversionData.time = 9;
            try {
                if (!socketMap.isEmpty()){
                    int count = 0 ;
                    for (Socket socket : socketMap.values()) {
                        if (socket!=null && socket!=socketMap.get("Algorithm")){
                            count++;
                            System.out.println(count+"::::"+socket.getPort());
                            socket.getOutputStream().write(OrderBase.FPS_100);
                        }
                    }
                }
                System.out.println("设置100帧率");
            }catch (Exception e){
                System.out.println("设置帧率失败");
                e.printStackTrace();
                System.out.println("设置帧率失败");
            }
        }
    }

}
