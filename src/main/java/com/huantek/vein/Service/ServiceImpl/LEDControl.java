package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.util.OrderBase;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

//LED控制
public class LEDControl {

    private ConcurrentHashMap<String,Socket> socketMap;

    public LEDControl(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    //判断传入参数调用对应方法
    public void ledService(JSONObject jsonObject){
        String action = jsonObject.getString("action");
        if (action!=null||!action.equals("")){
            if ("ledOffNode".equals(action)) ledOffNode(jsonObject);//关闭单个节点的LED
            if ("ledOffSuit".equals(action)) ledOffSuit(jsonObject);//关闭整套节点的LED
            if ("ledOffAll".equals(action)) ledOffAll();//关闭所有节点的LED
            if ("ledOpenNode".equals(action)) ledOpenNode(jsonObject);//打开单个节点的LED
            if ("ledOpenSuit".equals(action)) ledOpenSuit(jsonObject);//打开整套节点的LED
            if ("ledOpenAll".equals(action)) ledOpenAll();//打开所有节点的LED
            if ("ledFlashNode".equals(action)) ledFlashNode(jsonObject);//闪烁单个节点的LED
        }else {
            System.out.println("参数未传入");
        }
    }

    /**
     * 闪烁LED
     */

    //寻找节点：闪烁三次
    private void ledFlashNode(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        String mac = jsonObject.getString("MAC");
        if (suit.equals("") || suit==null) System.out.println("未传入套装参数");
        else if (mac.equals("") || mac==null) System.out.println("未传入MAC参数");
        else {
            try {
                String socketName = suit + mac;
                Socket socket = socketMap.get(socketName);//获取对应节点socket
                if (socket!=null){
                    socket.getOutputStream().write(OrderBase.LED_FLASH);//发送节点LED关闭指令
                    System.out.println("LED闪烁指令已发送");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 打开LED
     */

    //打开所有节点的LED
    private void ledOpenAll() {
        try {
            if (!socketMap.isEmpty()){
                int count = 0 ;
                for (Socket socket : socketMap.values()) {
                    if (socket!=null && socket!=socketMap.get("Algorithm")){
                        count++;
                        System.out.println(count+"::::"+socket.getPort());
                        socket.getOutputStream().write(OrderBase.LED_OPEN);
                    }
                }
            }
            System.out.println("LED打开指令已发送");
        }catch (Exception e){
            System.out.println("LED打开error");
            e.printStackTrace();
            System.out.println("LED打开error");
        }
    }

    //打开整套节点的LED
    private void ledOpenSuit(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        JSONArray macs = jsonObject.getJSONArray("MAC");
        String handySuit = jsonObject.getString("handySuit");
        JSONArray handyMACs = jsonObject.getJSONArray("handyMAC");
        try {
            if (suit!=null && !suit.equals("") && macs!=null){//Vein套装参数不为NULL就执行关闭LED命令
                for (Object mac : macs) {
                    String socketName = suit + mac;//拼接socket对应Key
                    Socket socket = socketMap.get(socketName);//获取对应节点的socket
                    if (socket!=null) socket.getOutputStream().write(OrderBase.LED_OPEN);//发送打开LED指令
                }
            }
            if (handySuit!=null && !handySuit.equals("") && handyMACs!=null){//Handy套装参数不为NULL就执行关闭LED命令
                for (Object handyMAC : handyMACs) {
                    String socketName = handySuit + handyMAC;//拼接socket对应Key
                    Socket socket = socketMap.get(socketName);//获取对应节点的socket
                    if (socket!=null) socket.getOutputStream().write(OrderBase.LED_OPEN);//发送打开LED指令
                }
            }
            System.out.println("LED打开指令已发送");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //打开单个节点的LED
    private void ledOpenNode(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        String mac = jsonObject.getString("MAC");
        if (suit.equals("") || suit==null) System.out.println("未传入套装参数");
        else if (mac.equals("") || mac==null) System.out.println("未传入MAC参数");
        else {
            try {
                String socketName = suit + mac;
                Socket socket = socketMap.get(socketName);//获取对应节点socket
                if (socket!=null){
                    socket.getOutputStream().write(OrderBase.LED_OPEN);//发送节点LED关闭指令
                    System.out.println("LED打开指令已发送");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    /**
     * 关闭LED
     */

    //关闭全部节点的LED
    private void ledOffAll() {
        int count = 0;
        try {
            if (!socketMap.isEmpty()){
                for (Socket socket : socketMap.values()) {
                    if (socket!=null && socket!=socketMap.get("Algorithm")){
                        count++;
                        System.out.println(count+"::::"+socket.getPort());
                        socket.getOutputStream().write(OrderBase.LED_OFF);
                    }
                }
            }
            System.out.println("LED关闭指令已发送");
        }catch (Exception e){

            System.out.println(count+"LED关闭error");
            e.printStackTrace();
            System.out.println("LED关闭error");
        }
    }

    //整套设备LED关闭
    private void ledOffSuit(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        JSONArray macs = jsonObject.getJSONArray("MAC");
        String handySuit = jsonObject.getString("handySuit");
        JSONArray handyMACs = jsonObject.getJSONArray("handyMAC");
        try {
            if (suit!=null && !suit.equals("") && macs!=null){//Vein套装参数不为NULL就执行关闭LED命令
                for (Object mac : macs) {
                    String socketName = suit + mac;//拼接socket对应Key
                    Socket socket = socketMap.get(socketName);//获取对应节点的socket
                    if (socket!=null) socket.getOutputStream().write(OrderBase.LED_OFF);//发送关闭LED指令
                }
            }
            if (handySuit!=null && !handySuit.equals("") && handyMACs!=null){//Handy套装参数不为NULL就执行关闭LED命令
                for (Object handyMAC : handyMACs) {
                    String socketName = handySuit + handyMAC;//拼接socket对应Key
                    Socket socket = socketMap.get(socketName);//获取对应节点的socket
                    if (socket!=null) socket.getOutputStream().write(OrderBase.LED_OFF);//发送关闭LED指令
                }
            }
            System.out.println("LED关闭指令已发送");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //单个节点LED关闭
    private void ledOffNode(JSONObject jsonObject) {
        String suit = jsonObject.getString("suit");
        String mac = jsonObject.getString("MAC");
        if (suit.equals("") || suit==null) System.out.println("未传入套装参数");
        else if (mac.equals("") || mac==null) System.out.println("未传入MAC参数");
        else {
            try {
                String socketName = suit + mac;
                Socket socket = socketMap.get(socketName);//获取对应节点socket
                if (socket!=null){
                    socket.getOutputStream().write(OrderBase.LED_OFF);//发送节点LED关闭指令
                    System.out.println("LED关闭指令已发送");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
