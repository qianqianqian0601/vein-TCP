package com.huantek.vein.socket;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.util.ParseDataFirm;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class SendDeviceList extends Thread{

    private SocketIOServer finalServer = null;
    public SendDeviceList(SocketIOServer server) {
        this.finalServer = server;
    }


    @Override
    public void run(){

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("control","deviceList");
                jsonObject.put("data",SocketThreadFirm.firmSuits);
                //System.out.println(SocketThreadFirm.firmSuits);
                finalServer.getBroadcastOperations().sendEvent("msgInfo",jsonObject);
            }
        },0,500);

    }

}
