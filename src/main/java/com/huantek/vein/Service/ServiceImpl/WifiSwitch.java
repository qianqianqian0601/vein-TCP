package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.util.OrderBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WifiSwitch {

    private ConcurrentHashMap<String,Socket> socketMap;

    public WifiSwitch(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }


    public void serviceWIFI(JSONObject jsonObject) throws IOException {
        String action = jsonObject.getString("action");
        if (action!=null|| !"".equals(action)){
            if ("updateSingleWIFI".equals(action)) updateSingleWIFI(jsonObject);
            if ("updateSuitWIFI".equals(action)) updateSuitWIFI(jsonObject);
            if ("clearSuitWIFI".equals(action)) clearSuitWIFI(jsonObject);
        }else {
            System.out.println("参数未传入");
        }
    }

    //清除wifi
    private void clearSuitWIFI(JSONObject jsonObject) {
        String suit = jsonObject.getString("productID");
        JSONArray macs = jsonObject.getJSONArray("MAC");
        try {
            if (suit!=null && !suit.equals("") && macs!=null){//Vein套装参数不为NULL就执行关闭LED命令
                for (Object mac : macs) {
                    String socketName = suit + mac;//拼接socket对应Key
                    Socket socket = socketMap.get(socketName);//获取对应节点的socket
                    if (socket!=null) socket.getOutputStream().write(OrderBase.CLEAR_WIFI);//wifi清除命令
                }
            }
            System.out.println("清除WIFI指令已发送");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //切换单个节点wifi
    private void updateSuitWIFI(JSONObject jsonObject) throws IOException {
        String productID = jsonObject.getString("productID");
        String wifiName = jsonObject.getString("wifiName");
        String wifiPassword = jsonObject.getString("wifiPassword");
        JSONArray macs = jsonObject.getJSONArray("MAC");
        for (Object mac : macs) {
            String socketName = productID + mac;
            updateWIFI(socketName,wifiName,wifiPassword);
        }
    }

    //切换整套节点wifi
    private void updateSingleWIFI(JSONObject jsonObject) throws IOException {
        String productID = jsonObject.getString("productID");
        String mac = jsonObject.getString("MAC");
        String wifiName = jsonObject.getString("wifiName");
        String wifiPassword = jsonObject.getString("wifiPassword");
        String socketName = productID + mac;
        updateWIFI(socketName,wifiName,wifiPassword);
    }

    private void updateWIFI(String socketName,String wifiName,String wifiPassword) throws IOException {
        Socket socket = socketMap.get(socketName);
        if (socket==null){
            System.out.println("节点未连接");
            return;
        }else {
            byte[] wifiNameArr = ByteZeroFill(wifiName.getBytes(), 32);
            byte[] wifiPasswordArr = ByteZeroFill(wifiPassword.getBytes(), 64);
            byte len = (byte) (wifiNameArr.length + wifiPasswordArr.length);
            byte[] head = {0x47,0x07,len};
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            byteArr.write(head,0,head.length);
            byteArr.write(wifiNameArr,0,wifiNameArr.length);
            byteArr.write(wifiPasswordArr,0,wifiPasswordArr.length);
            byteArr.write(0x74);
            byte[] bytes = byteArr.toByteArray();
            socket.getOutputStream().write(bytes);
            System.out.println("------设备切换WIFI:---"+wifiName+"--命令已发送");
        }

    }


    /**
     * 数组补零
     * @param bytes 需要补零的数组
     * @param len  最终长度
     * @return
     */
    public static byte[] ByteZeroFill(byte[] bytes , int len){
        if(bytes.length<len){
            byte[] arr = new byte[len];
            for (int i = 0;i<bytes.length;i++){
                arr[i] = bytes[i];
            }
            return arr;
        }else {
            return bytes;
        }
    }
}
