package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.socket.SocketServer;
import com.huantek.vein.util.OrderBase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//设备控制
public class DMClass {

    ConcurrentHashMap<String,Socket> socketMap = null;

    public DMClass(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }


    //根据参数调用应方法
    public void deviceManagement(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if (action==null||action.equals("")){
            System.out.println("参数未传入");
        }else {
            if ("offNode".equals(action)) powerOffNode(jsonObject);//单个节点关机
            if ("offAll".equals(action)) powerOffAll();//全部关机
            if ("offSuit".equals(action)) powerOffSuit(jsonObject);//整套节点关机
            if ("rebootNode".equals(action)) powerRebootNode(jsonObject);//单个节点重启
        }
    }


    /**
     * 重启
     */

    //单个节点重启
    private void powerRebootNode(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        String mac = jsonObject.getString("MAC");
        if (suit.equals("")||suit==null) System.out.println("套装参数未传入");
        else if (mac.equals("")||mac==null) System.out.println("节点参数未传入");
        else {
            try {
                String socketName = suit + mac;
                Socket socket = socketMap.get(socketName);
                if (socket!=null) {
                    socket.getOutputStream().write(OrderBase.REBOOT_COM);
                    System.out.println("重启指令已发送");
                } else {
                    System.out.println("节点未连接");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 关机
     */

    //整套设备节点关机
    private void powerOffSuit(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");//Vein套装ID
        JSONArray macs = jsonObject.getJSONArray("MAC");//Vein节点数组
        String handySuit = jsonObject.getString("handySuit");//handy套装ID
        JSONArray handyMACS = jsonObject.getJSONArray("handyMAC");//handy节点数组
        try {
            if (suit!=null&&!suit.equals("")&&macs!=null){//Vein套装参数不NULL执行Vein关闭
                for (Object mac : macs) {
                    String VeinSocketName = suit + mac;//拼接Vein的socket节点Name
                    Socket socket = socketMap.get(VeinSocketName);//获取对应的Socket
                    if (socket!=null) {
                        socket.getOutputStream().write(OrderBase.OFF_COM);//发送关机指令
                        socket.close();//关闭socket连接
                        socketMap.remove(VeinSocketName);//移除socket集合中关机的socket
                    }
                }
                System.out.println("关机指令已发送");
            }
            if (handySuit!=null&&!handySuit.equals("")&&handyMACS!=null){//handy套装参数不NULL执行handy关闭
                for (Object handyMAC : handyMACS) {
                    String handySocketName = handySuit + handyMAC;//拼接handy的socket节点Name
                    Socket socket = socketMap.get(handySocketName);//获取对应的Socket
                    if (socket!=null) {
                        socket.getOutputStream().write(OrderBase.OFF_COM);//发送关机指令
                        socket.close();//关闭socket连接
                        socketMap.remove(handySocketName);//移除socket集合中关机的socket
                    }
                    System.out.println("关机指令已发送");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //设备单个节点关机
    private void powerOffNode(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        String mac = jsonObject.getString("MAC");
        if (suit.equals("")||suit==null) System.out.println("未指定套装");
        else if (mac.equals("")||mac==null) System.out.println("未指定节点");
        else {
            String socketName = suit + mac;
            Socket socket = socketMap.get(socketName);//获取对应节点socket
            try {
                if (socket != null) {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(OrderBase.OFF_COM);
                    socket.close();//关闭socket连接
                    socketMap.remove(socketName);//移除socket集合中关机的socket
                    System.out.println("关机指令已发送");
                } else {
                    System.out.println("节点未连接");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //关闭所有设备
    public void powerOffAll() {
        try {
            if (!socketMap.isEmpty()){
                int count = 0;
                Iterator<Map.Entry<String, Socket>> iterator = socketMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Socket> next = iterator.next();
                    count++;
                    System.out.println(count+"::::"+next.getKey());
                    Socket socket = next.getValue();
                    socket.getOutputStream().write(OrderBase.OFF_COM);
                    iterator.remove();
                }
                System.out.println("关机指令已发送");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
