package com.huantek.vein.socket;

import com.huantek.vein.util.OrderBase;
import lombok.SneakyThrows;

import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


//定时发送查询命令到传感器，查询传感器电量和信号，电量一分钟一次，传感器一秒一次。
public class SendFirmMsgThread extends Thread{
    //创建和本线程相关的Socket
    private ConcurrentHashMap<String,Socket> socketMap = null;
    private int count;
    public static int num = 0 , num3 = 0;


    public SendFirmMsgThread(ConcurrentHashMap<String, Socket> socketMap, int numBer) {
        this.socketMap = socketMap;
        this.count = numBer;
    }


    @Override
    public void run(){
        Socket socket = socketMap.get("Firmware" + count);
        if (socket!=null){
            Socket finalSocket = socket;//变量“socket”从内部类访问，需要是final或有效的final
            try {
                new Timer().schedule(new TimerTask() {//定时器定时5s发送心跳包
                    @SneakyThrows
                    @Override
                    public void run() {
                        OutputStream outputStream = null;
                        try {
                            outputStream = finalSocket.getOutputStream();
                            outputStream.write(OrderBase.HEART_BEAT);
                        } catch (Exception e) {
                            if (outputStream != null) outputStream.close();
                            if (finalSocket != null) finalSocket.close();
                        }
                    }
                },0,2000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
