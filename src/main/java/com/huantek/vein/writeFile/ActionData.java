package com.huantek.vein.writeFile;

import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.util.PublicVariable;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActionData extends Thread{
    String url;
    private File actionDataFile;
    public void  params(JSONObject jsonObject) {
        this.url = jsonObject.getString("url");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String startTime = sdf.format(new Date());
        actionDataFile = new File(url+startTime+".txt");
    }

    private long time = 15;
    FileWriter writer;



    private void writeActionData() throws IOException {
        if (!actionDataFile.exists()){
            writer = new FileWriter(actionDataFile);
            actionDataFile.createNewFile();
        }else {
            writer = new FileWriter(actionDataFile,true);
        }
        writer.write(PublicVariable.VeinData.toJSONString());
        writer.write("\r\n");
        writer.flush();
    }


    @SneakyThrows
    @Override
    public void run() {
        while (true){
            synchronized (actionDataFile){
                if ("start".equals(PublicVariable.recordThread)){
                    PublicVariable.recordFrameCount++;
                    writeActionData();
                    sleep(time);
                } else if ("stop".equals(PublicVariable.recordThread)){
                    if (writer!=null){
                        writer.close();
                    }
                    break;
                }
            }
        }
    }
}
