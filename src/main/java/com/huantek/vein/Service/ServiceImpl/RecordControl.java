package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.util.PublicVariable;
import com.huantek.vein.writeFile.ActionData;
import com.huantek.vein.writeFile.DeviceList;

public class RecordControl {

    public void record(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if (!"".equals(action) || action != null){
            if ("start".equals(action)) startRecord(jsonObject);
            if ("stop".equals(action)) stopRecord();
        }else {
            System.out.println("参数未传入");
        }
    }

    public static long StartTime,EndTime;
    private void startRecord(JSONObject jsonObject) {
        PublicVariable.recordThread = "start";
        PublicVariable.recordFrameCount = 0;
        StartTime = System.currentTimeMillis();
        DeviceList deviceList = new DeviceList();
        deviceList.params(jsonObject);
        ActionData actionData = new ActionData();
        actionData.params(jsonObject);
        deviceList.start();
        actionData.start();
    }

    private void stopRecord(){
        EndTime = System.currentTimeMillis();
        PublicVariable.recordThread = "stop";
    }
}
