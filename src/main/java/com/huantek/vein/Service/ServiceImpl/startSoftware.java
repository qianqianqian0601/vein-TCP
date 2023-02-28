package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.jni.conversionData.ConversionData;
import com.huantek.jni.conversionData.ConversionDataHandy2;
import com.huantek.vein.socket.SocketServer;
import com.huantek.vein.util.PublicVariable;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

@Slf4j
public class startSoftware {
    SocketIOServer server;

    public startSoftware(SocketIOServer socketIOServer) {
        this.server = socketIOServer;
    }

    public void startup(JSONObject jsonObject) {
        String software = jsonObject.getString("software");
        try {
            if (SocketServer.softwareFlag.equals("")) {
                SocketServer.softwareFlag = software;
                if ("Handy2".equals(software)) {
                    startHandy2();
                } else if ("Vein".equals(software)) {
                    startVein();
                } else {
                    log.debug("请勿启动非法程序...");
                }
            } else {
                if (SocketServer.softwareFlag.equals(software)) {
                    log.debug("请勿重复启动" + software + "程序！");
                } else {
                    if (software.equals("Vein") && SocketServer.softwareFlag.equals("Handy2") ) {
                        if(PublicVariable.veins){
                            SocketServer.softwareFlag = software;
                        }else {
                            SocketServer.softwareFlag = software;
                            startVein();
                        }
                    } else if (software.equals("Handy2") && SocketServer.softwareFlag.equals("Vein")) {
                        if (PublicVariable.handy2){
                            SocketServer.softwareFlag = software;
                        }else {
                            SocketServer.softwareFlag = software;
                            startHandy2();
                        }
                    } else {
                        log.debug("程序已全部启动!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject object = new JSONObject();
        object.put("control", "software");
        object.put("status", "ok");
        server.getBroadcastOperations().sendEvent("msgInfo", object);
    }

    public void startHandy2() {
        PublicVariable.handy2 = true;
        ConversionDataHandy2 conversionDataHandy2 = new ConversionDataHandy2(server);//动作数据转换
        SocketServer.veinDataExecutor.execute(conversionDataHandy2);
        System.out.println("启动了handy2程序！");
    }

    public void startVein() throws Exception {
        PublicVariable.veins = true;
        ConversionData conversionData = new ConversionData(server);//动作数据转换
        SocketServer.veinDataExecutor.execute(conversionData);
        sleep(10);
        log.debug("线程优先级：" + conversionData.getPriority());
        log.debug("启动了VEIN程序！");
        log.debug("softwareFlag！" + SocketServer.softwareFlag);
    }
}
