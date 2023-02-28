package com.huantek.vein.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.jni.conversionData.ConversionData;
import com.huantek.jni.conversionData.KeyFrameInterpolation;
import com.huantek.vein.Model.FirmSuit;
import com.huantek.vein.util.*;
import com.huantek.vein.util.TestPort;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.*;

@Component("SocketServer")
public class SocketServer implements ApplicationListener<ContextRefreshedEvent> {

    private static ServerSocket serverSocket;
    public static boolean OFF_ON = true;
    public static String softwareFlag = "";
    public static ExecutorService veinDataExecutor = Executors.newFixedThreadPool(10);

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            // TODO Auto-generated method stub
            socketSeverStart();
        }
    public static void socketSeverStart() throws UnknownHostException {
        ConcurrentHashMap<String, Socket> socketMap = PublicVariable.socketMap;//获取公共的socket集合
            InetAddress local;
            int [] socketPortArray = {9999,9977,7777,4444,9944,7744};
            int [] websocketPortArray = {30000,30001,30002,30003,30004,30005};
            TestPort testPort = new TestPort();
            int socketPort = 0 , websocketPort = 0;
            for (int port : socketPortArray) {
                boolean flag = testPort.isLocalePortUsing(port);
                if (flag == false){
                    socketPort = port;
                    break;
                }
            }
            for (int port : websocketPortArray) {
                boolean flag = testPort.isLocalePortUsing(port);
                if (flag == false){
                    websocketPort = port;
                    break;
                }
            }
            Configuration config = new Configuration();//new 一个配置项
            config.setHostname("127.0.0.1");//设置本机IP
            config.setPort(websocketPort);//设置监听端口
            config.setMaxFramePayloadLength(1024 * 1024);
            config.setMaxHttpContentLength(1024 * 1024);
            SocketIOServer server = new SocketIOServer(config);//new websocketIO服务
            server.start();//启动websocketIO
            System.out.println("WebSocket启动，等待PC连接...");
            try {
                serverSocket = new ServerSocket(socketPort);
            }catch (Exception e){
                System.out.println("serverSocket异常！");
                e.printStackTrace();
            }
            System.out.println("服务端启动成功,websocket端口:"+websocketPort+"    socket端口:"+socketPort);

            WebSocketServer webSocketServer2 = new WebSocketServer(server,socketMap,serverSocket,socketPort);
            webSocketServer2.socketStart();

            CopyOnWriteArrayList<FirmSuit> FirmSuits = IntegratedData.initFirmSuitList();//初始化设备列表数组
            SendDeviceList sendDeviceList = new SendDeviceList(server);//初始化发送设备列表方法
            sendDeviceList.start();//开始发送设备列表
        new Thread(()->{
                int count = 0,numBer = 0;//count记录客户端连接个数，count记录固件连接个数;
                try {
                        while (OFF_ON){
                        Socket socket = serverSocket.accept();//调用accept()方法，开始监听，等待客户端连接
                         socket.setTcpNoDelay(true);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String channelToken = bufferedReader.readLine();
                        if(channelToken!=null){
                            if ("U3D".equals(channelToken)){//识别算法socket
                                System.out.println("U3D连接----");
                                socketMap.put(channelToken,socket);//读入字节流，存入相应的socket在Map中
                                SendVeinData sendVeinData = new SendVeinData(socketMap);//创建socket发送线程
                                veinDataExecutor.execute(sendVeinData);
                            }
                            if ("Firmware".equals(channelToken)){//识别传感器socket
                                numBer++;
                                count++;
                                channelToken+=numBer;
                                socketMap.put(channelToken,socket);//读入字节流，存入相应的socket在Map中
                                SocketThreadFirm socketThread = new SocketThreadFirm(server,socketMap,numBer,count,FirmSuits);//创建Socket线程
                                SendFirmMsgThread sendFirmMsgThread = new SendFirmMsgThread(socketMap, numBer);//创建socket发送线程
                                veinDataExecutor.execute(socketThread);
                                veinDataExecutor.execute(sendFirmMsgThread);
                            }
                            if (count!=0){
                                System.out.println("固件连接的数量："+count);
                            }
                            if (count==1){
                                RecordFrameRate recordFrameRate = new RecordFrameRate();
                                veinDataExecutor.execute(recordFrameRate);
                                startSendData startSendData = new startSendData();
                                startSendData.sendDataEvent();
                            }
                            InetAddress inetAddress = socket.getInetAddress();
                            String hostAddress = inetAddress.getHostAddress();//获取连接客户端的IP
                            System.out.println("Socket客户端：---"+hostAddress);
                        }
                    }
                    server.stop();
                    serverSocket.close();
                    BeforeCloseTreatment.dosClose();
                } catch (Exception e) {
                    System.out.println("socketServer");
                    e.printStackTrace();
                }
                //需要死循环持续监听客户端传来的消息
                //serverSocket.close();
            }).start();
    }


}
