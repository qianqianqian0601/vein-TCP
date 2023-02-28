package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.util.OrderBase;

import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class pickUpControl {
    private ConcurrentHashMap<String,Socket> socketMap;
    public pickUpControl(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    public void pickUpCalibration(JSONObject jsonObject){
        String action = jsonObject.getString("action");
        if (null != action && action!=""){
            if (action.equals("pickUpAll")) pickUpAll();
            if (action.equals("pickUpNode")) pickUpNode(jsonObject);
            if (action.equals("pickUpSuit")) pickUpSuit(jsonObject);
        }
    }

    //套装传感器校准
    private void pickUpSuit(JSONObject jsonObject) {
        String suit = jsonObject.getString("productID");
        JSONArray macs = jsonObject.getJSONArray("mac");
        if (suit.equals("") || suit==null) System.out.println("未传入套装参数");
        else if (macs==null) System.out.println("未传入MAC参数");
        else {
            try {
                for (Object mac : macs) {
                    String socketName = suit + mac;
                    Socket socket = socketMap.get(socketName);
                    socket.getOutputStream().write(OrderBase.PICKUP_CALIBRATION);
                }
                System.out.println(suit+"套装传感器校准已发送");
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("传感器校准异常");
            }
        }
    }

    //节点传感器校准
    private void pickUpNode(JSONObject jsonObject) {
        String suit = jsonObject.getString("productID");
        String mac = jsonObject.getString("mac");
        if (suit.equals("") || suit==null) System.out.println("未传入套装参数");
        else if (!mac.equals("")||mac==null) System.out.println("未传入MAC参数");
        else {
            try {
                String socketName = suit + mac;
                Socket socket = socketMap.get(socketName);
                socket.getOutputStream().write(OrderBase.PICKUP_CALIBRATION);
                System.out.println("节点传感器校准已发送");
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("传感器校准异常");
            }

        }
    }

    //所有传感器校准
    private void pickUpAll(){
        try {
            if (!socketMap.isEmpty()){
                Iterator<Map.Entry<String, Socket>> iterator = socketMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Socket> next = iterator.next();
                    next.getValue().getOutputStream().write(OrderBase.PICKUP_CALIBRATION);
                    System.out.println("传感器校准命令已发送");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("传感器校准异常");
        }
    }
}
