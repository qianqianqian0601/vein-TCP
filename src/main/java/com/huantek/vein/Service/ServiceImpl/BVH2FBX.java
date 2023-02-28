package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.File;

public class BVH2FBX {
    private SocketIOServer server;
    public BVH2FBX(SocketIOServer socketIOServer) {
        this.server = socketIOServer;
    }

    /**
     *检模块函数映射
     */
    public interface CLibrary extends Library {
        CLibrary INSTANCE =
                (CLibrary) Native.loadLibrary(
                        "BVH2FBX",
                        CLibrary.class);
        int bvh2fbx(String bvhFileName, String fbxFileName);
    }

    public void toFBX(JSONObject jsonObject) {
        JSONObject msgInfo = new JSONObject();
        msgInfo.put("control","BVH2FBX");
        String bvhPath = jsonObject.getString("bvhPath");
        File bvhFile = new File(bvhPath);
        if (!bvhFile.exists()){
            msgInfo.put("object","BVH2FBX");
            msgInfo.put("success",0);
            msgInfo.put("info","BVH文件不存在！");
            server.getBroadcastOperations().sendEvent("msgInfo",msgInfo);
            return;
        }
        String fbxPath = jsonObject.getString("fbxPath");
        int indexOf = fbxPath.lastIndexOf("\\");
        String path = fbxPath.substring(0,indexOf + 1);
        File fbxFile = new File(path);
        if (!fbxFile.exists()){
            msgInfo.put("object","BVH2FBX");
            msgInfo.put("success",0);
            msgInfo.put("info","FBX文件输出路径不存在！");
            server.getBroadcastOperations().sendEvent("msgInfo",msgInfo);
            return;
        }
        int bool = CLibrary.INSTANCE.bvh2fbx(bvhPath, fbxPath);
        if (bool==1){
            msgInfo.put("object","BVH2FBX");
            msgInfo.put("success",1);
            server.getBroadcastOperations().sendEvent("msgInfo",msgInfo);
        }else {
            msgInfo.put("object","BVH2FBX");
            msgInfo.put("success",0);
            server.getBroadcastOperations().sendEvent("msgInfo",msgInfo);
        }
    }
}
