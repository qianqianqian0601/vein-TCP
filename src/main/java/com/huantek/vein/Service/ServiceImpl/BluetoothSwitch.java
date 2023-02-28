package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huantek.vein.socket.socketHandyBluetooth;
import com.huantek.vein.util.PublicVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BluetoothSwitch {


    private SocketIOServer socketIOServer;
    public BluetoothSwitch(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    public void serviceBluetooth(JSONObject jsonObject) throws IOException {
        String deviceId = jsonObject.getString("deviceId");
        String str = jsonObject.getString("data");
        JsonNode pfd = new ObjectMapper().readTree(str).get("data");// 得到data[]数据

        socketHandyBluetooth socketHandyBluetooth = new socketHandyBluetooth(socketIOServer);
        String pfds = String.valueOf(pfd);
        String substring = pfds.substring(1);
        String substring1 = substring.substring(0,substring.length()-1);
        String[] split = substring1.split(",");//获取[]里面的数据

        int index = 0;
        List<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i <split.length ; i++) {
            index = Integer.parseInt(split[i]);
            arrayList.add(index);
        }

        socketHandyBluetooth.bluetoothSwitchData(deviceId , arrayList);
    }
}

