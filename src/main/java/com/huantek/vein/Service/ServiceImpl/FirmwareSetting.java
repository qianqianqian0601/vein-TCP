package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.Model.FirmNode;
import com.huantek.vein.Model.FirmSuit;
import com.huantek.vein.socket.SocketThreadFirm;
import com.huantek.vein.util.DataConversionUtils;
import com.huantek.vein.util.OrderBase;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//设备配置
public class FirmwareSetting {
    private ConcurrentHashMap<String,Socket> socketMap;
    private SocketIOServer webSocket;
    public FirmwareSetting(ConcurrentHashMap<String, Socket> socketMap, SocketIOServer socketIOServer) {
        this.socketMap = socketMap;
        this.webSocket = socketIOServer;
    }

    private final String control = "exception";

    //根据参数获取调用方法
    public void firmwareDetailSetting(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if (action!=null&&!action.equals("")){
            if ("nodeSetUP".equals(action)) nodeSetUP(jsonObject);//调用节点配置方法
            if ("productIDSetUP".equals(action)) productIDSetUP(jsonObject);//调用产品ID配置方法
            if ("productIDUnbind".equals(action)) productIDUnbind(jsonObject);//调用产品ID配置方法
        }else {
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
        }
    }

    //产品ID配置方法
    private void productIDSetUP(JSONObject jsonObject) {
        String productID = jsonObject.getString("productID");
        String mac = jsonObject.getString("MAC");
        int newProductID = jsonObject.getInteger("newProductID");
        byte[] permissionCode = {0x66,0x66,0x66,0x66};
        if (productID==null||productID.equals("")) {
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }
        if (mac==null||mac.equals("")) {
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }
        try {
            String socketName = productID + mac;
            byte[] arr = {0x47,0x09,0x08};//帧头和控制域以及长度
            byte[] chai = DataConversionUtils.intToByteArraySmall(newProductID);//产品ID
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(arr,0,arr.length);//拼接帧头
            bos.write(permissionCode,0,permissionCode.length);//拼接权限码
            bos.write(chai,0,chai.length);//拼接产品ID
            bos.write(0x74);//拼接帧尾
            byte[] bytes = bos.toByteArray();//生成新的byte[]
            Socket socket = socketMap.get(socketName);//获取socket
            System.out.println(Arrays.asList(bos));
            if (socket!=null){
                socket.getOutputStream().write(bytes);
                System.out.println("产品ID配置指令已发送");

                Iterator<FirmSuit> iterator = SocketThreadFirm.firmSuits.iterator();
                while (iterator.hasNext()){
                    FirmSuit firmSuit = iterator.next();
                    if (firmSuit.getProductID().equals(productID)){
                        CopyOnWriteArrayList<FirmNode> firmNodes = firmSuit.getFirmNodes();//获取该套装节点
                        for (int i = 0; i < firmNodes.size(); i++) {
                            if (firmNodes.get(i)!=null && firmNodes.get(i).getMAC().equals(mac)){//将数组中原节点详情改为null
                                if (i>=17){
                                    firmNodes.remove(i);
                                }else {
                                    firmNodes.set(i,null);
                                }
                            }
                        }
                        boolean flag = nodesIsEmpty(firmSuit);//查看套装中的有没有节点存在
                        if (flag){
                            SocketThreadFirm.firmSuits.remove(firmSuit);
                            if (SocketThreadFirm.firmSuits.isEmpty()){
                                FirmSuit initFirmSuit = new FirmSuit();
                                initFirmSuit.setProductID("");
                                initFirmSuit.setFirmNodes(null);
                                SocketThreadFirm.firmSuits.add(initFirmSuit);
                            }
                        }
                    }
                }
                socket.getOutputStream().write(OrderBase.QUERY_FIRMWARE_INFO);//查询设备详情
            }else {
                System.out.println("节点未连接");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //节点配置方法
    private void nodeSetUP(JSONObject jsonObject) {
        String productID = jsonObject.getString("productID");
        String mac = jsonObject.getString("MAC");
        int newNode = jsonObject.getInteger("newNode");
        if (newNode<0||newNode>17){
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }
        if (productID==null||productID.equals("")){
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }
        if (mac==null||mac.equals("")){
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }
        try {
            String socketName = productID + mac;
            byte[] arr = {0x47,0x08,0x01,(byte)newNode,0x74};
            Socket socket = socketMap.get(socketName);
            if (socket!=null){
                socket.getOutputStream().write(arr);
                System.out.println("节点配置指令已发送");

                Iterator<FirmSuit> iterator = SocketThreadFirm.firmSuits.iterator();
                while (iterator.hasNext()){
                    FirmSuit firmSuit = iterator.next();
                    if (firmSuit.getProductID().equals(productID)){
                        CopyOnWriteArrayList<FirmNode> firmNodes = firmSuit.getFirmNodes();//获取该套装节点
                        for (int i = 0; i < firmNodes.size(); i++) {
                            if (firmNodes.get(i)!=null && firmNodes.get(i).getMAC().equals(mac)){//将数组中原节点详情改为null
                                if (i>=17){
                                    firmNodes.remove(i);
                                }else {
                                    firmNodes.set(i,null);
                                }
                            }
                        }
                        boolean flag = nodesIsEmpty(firmSuit);//查看套装中的有没有节点存在
                        if (flag){
                            SocketThreadFirm.firmSuits.remove(firmSuit);
                            if (SocketThreadFirm.firmSuits.isEmpty()){
                                FirmSuit initFirmSuit = new FirmSuit();
                                initFirmSuit.setProductID("");
                                initFirmSuit.setFirmNodes(null);
                                SocketThreadFirm.firmSuits.add(initFirmSuit);
                            }
                        }
                    }
                }
                socket.getOutputStream().write(OrderBase.QUERY_FIRMWARE_INFO);//查询设备详情
            }else {
                System.out.println("节点未连接");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //产品ID解绑方法
    private void productIDUnbind(JSONObject jsonObject) {
        String productID = jsonObject.getString("productID");
        String mac = jsonObject.getString("MAC");
        if (productID==null||productID.equals("")){
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }
        if (mac==null||mac.equals("")){
            JSONObject exceptionJson = new JSONObject();
            exceptionJson.put("control",control);
            exceptionJson.put("statusCode","301");
            webSocket.getBroadcastOperations().sendEvent("msgInfo",exceptionJson);
            return;
        }

        try {
            String socketName = productID + mac;
            byte[] arr2 = {0x47,0x09,0x08,0x66,0x66,0x66,0x66,0x00,0x00,0x00,0x00,0x74};//解绑产品ID 改0
            Thread.sleep(200);
            byte[] arr1 = {0x47,0x08,0x01,0x00,0x74};//节点改成0
            Socket socket = socketMap.get(socketName);
            if (socket!=null){
                socket.getOutputStream().write(arr1);
                Thread.sleep(300);
                socket.getOutputStream().write(arr2);
                System.out.println("产品ID解绑指令已发送");

                Iterator<FirmSuit> iterator = SocketThreadFirm.firmSuits.iterator();
                while (iterator.hasNext()){
                    FirmSuit firmSuit = iterator.next();
                    if (firmSuit.getProductID().equals(productID)){
                        CopyOnWriteArrayList<FirmNode> firmNodes = firmSuit.getFirmNodes();//获取该套装节点
                        for (int i = 0; i < firmNodes.size(); i++) {
                            if (firmNodes.get(i)!=null && firmNodes.get(i).getMAC().equals(mac)){//将数组中原节点详情改为null
                                if (i>=17){
                                    firmNodes.remove(i);
                                }else {
                                    firmNodes.set(i,null);
                                }
                            }
                        }
                        boolean flag = nodesIsEmpty(firmSuit);//查看套装中的有没有节点存在
                        if (flag){
                            SocketThreadFirm.firmSuits.remove(firmSuit);
                            if (SocketThreadFirm.firmSuits.isEmpty()){
                                FirmSuit initFirmSuit = new FirmSuit();
                                initFirmSuit.setProductID("");
                                initFirmSuit.setFirmNodes(null);
                                SocketThreadFirm.firmSuits.add(initFirmSuit);
                            }
                        }
                    }
                }
                socket.getOutputStream().write(OrderBase.QUERY_FIRMWARE_INFO);//查询设备详情
            }else {
                System.out.println("节点未连接");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * //判断节点是否为NULL,true为NULL,false不为NULL
     * @param suit
     * @return
     */
    private static boolean nodesIsEmpty(FirmSuit suit){
        CopyOnWriteArrayList<FirmNode> firmNodes = suit.getFirmNodes();
        for (FirmNode firmNode : firmNodes) {
            if (firmNode!=null){
                return false;
            }
        }
        return true;
    }


    private static boolean suitIsExist(String productID){
        for (FirmSuit firmSuit : SocketThreadFirm.firmSuits) {
            if (productID.equals(firmSuit.getProductID())) return true;
        }
        return false;
    }
}
