package com.huantek.vein.util;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ready {
    SocketIOServer server;

    private boolean readyFlag = false;

    public Ready(SocketIOServer server) {
        this.server = server;
    }

    public void yes(JSONObject jsonObject) {
        boolean ready = jsonObject.getBoolean("ready");
        if (readyFlag == false) {
            readyFlag = ready;
            JSONObject object = new JSONObject();
            object.put("control", "ready");
            object.put("status", "yes");
            server.getBroadcastOperations().sendEvent("msgInfo", object);
            log.debug("后端已准备就绪...");
        } else {
            log.debug("准备就绪防止重复操作...");
        }
    }
}
