package com.huantek.vein.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class startSendData extends Thread {

    public void sendDataEvent() {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    //500ms没有新的连接逻辑处理
                    //500ms没有新的连接 开始发送动作数据
                    if (PublicVariable.newConnectTime != 0) {
                        long timeMillis = System.currentTimeMillis();
                        long l = timeMillis - PublicVariable.newConnectTime;
                        if (l > 500 && PublicVariable.connectFlag == false) {
                            long count = PublicVariable.socketMap.values().stream().count();
                            if (count== PublicVariable.socketMap.size()){
                                for (Socket socket : PublicVariable.socketMap.values()) {
                                    //发送停止指令
                                    try {
                                        socket.getOutputStream().write(OrderBase.START_MOTION_CAPTURE);
                                        log.debug(":开始发送数据");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                PublicVariable.connectFlag = true;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 200);
    }


}
