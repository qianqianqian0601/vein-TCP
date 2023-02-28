package com.huantek.vein.socket;


import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.Service.ServiceImpl.*;
import com.huantek.vein.util.BeforeCloseTreatment;
import com.huantek.vein.util.Ready;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class WebSocketServer {


    SocketIOServer socketIOServer;
    ConcurrentHashMap<String,Socket> socketMap;
    ServerSocket serverSocket;
    int socketPort;
    public WebSocketServer(SocketIOServer server, ConcurrentHashMap<String, Socket> socketMap, ServerSocket serverSocket, int socketPort) {
        this.socketIOServer = server;
        this.socketMap = socketMap;
        this.serverSocket = serverSocket;
        this.socketPort = socketPort;
    }

    public void socketStart(){

        //连接监听
        socketIOServer.addConnectListener(client -> {
            String clientInfo = client.getRemoteAddress().toString();
            String clientIp = clientInfo.substring(1, clientInfo.indexOf(":"));
            client.sendEvent("cliented","IP:"+clientIp);
        });

        //断开连接监听
        socketIOServer.addDisconnectListener(client -> {
            String clientInfo = client.getRemoteAddress().toString();
            String clientIp = clientInfo.substring(1, clientInfo.indexOf(":"));
            client.sendEvent("disconnect","IP:"+clientIp);
            //new BeforeCloseTreatment(serverSocket,socketMap).recycling();
        });

        socketIOServer.addEventListener("msgInfo", String.class, (client, data, ackRequest) -> {
            String clientInfo = client.getRemoteAddress().toString();
            String clientIp = clientInfo.substring(1, clientInfo.indexOf(":"));
            System.out.println("WebSocket客户端"+clientIp+": "+data);
            JSONObject jsonObject = JSONObject.parseObject(data);
            String control = jsonObject.getString("control");
            //String jsonMessage = null;
            //根据control参数进行判断调用方法
            if ("calibrate".equals(control)) new ActionCalibrate(socketIOServer,socketMap).VeinCalibrate(jsonObject);//动作校准
            if ("firmwareVersionUp".equals(control)) new FVClass(socketMap).firmwareVersion(jsonObject);//动作校准
            if ("switch".equals(control)) new DMClass(socketMap).deviceManagement(jsonObject);//开关控制
            if ("firmwareSetting".equals(control)) new FirmwareSetting(socketMap,socketIOServer).firmwareDetailSetting(jsonObject);
            if ("ledControl".equals(control)) new LEDControl(socketMap).ledService(jsonObject);//led控制
            if ("BVHControl".equals(control)) new BVHMClass(socketIOServer).writeBVH(jsonObject);//BVH录制
            if ("testControl".equals(control)) new TestSendAndReceive(socketMap).testCommunicationService(jsonObject);//通信耗时测试
            if ("wifiSwitch".equals(control)) new WifiSwitch(socketMap).serviceWIFI(jsonObject);//切换WIFI
            if ("handyBluetoothData".equals(control)) new BluetoothSwitch(socketIOServer).serviceBluetooth(jsonObject);//Handy2 蓝牙
            if ("recordControl".equals(control)) new RecordControl().record(jsonObject);//录制
            if ("changeSize".equals(control)) new ChangeSize().changeModelSize(jsonObject);//改变模型大小
            if ("closeServer".equals(control)) new BeforeCloseTreatment().recycling();//关闭服务
            if ("streamingControl".equals(control)) new StreamingData(socketIOServer).streaming(jsonObject);//串流数据
            if ("modelControl".equals(control)) new ModelControl().modelCommand(jsonObject);//模型控制
            if ("dataQueueCount".equals(control)) new DataQueue().dataQueueCount();//当前数据缓存帧数
            if ("keyboard".equals(control)) new pressKeyboard().inputKeyCode(jsonObject);//键入键盘
            if ("handyMapping".equals(control)) new handyMapping().handyAssort(jsonObject);//handy配套
            if ("frameControl".equals(control)) new frameControl(socketMap).FPS(jsonObject);//handy配套
            if ("pickUpCalibration".equals(control)) new pickUpControl(socketMap).pickUpCalibration(jsonObject);//传感器校准
            if ("addServer".equals(control)) new addServer().JmdNSService(jsonObject,socketPort);//JmDNS服务注册
            if ("BVH2FBX".equals(control)) new BVH2FBX(socketIOServer).toFBX(jsonObject);//BVH2FBX
            if ("startSoftware".equals(control)) new startSoftware(socketIOServer).startup(jsonObject);
            if ("isReady".equals(control)) new Ready(socketIOServer).yes(jsonObject);//告诉前端后端已经准备就绪
        });
    }



}