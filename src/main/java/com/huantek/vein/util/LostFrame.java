package com.huantek.vein.util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class LostFrame {


    public static void lostFramesRecord(JSONObject nodeData,int number) throws IOException {//nodeData解析的动数据加帧率，number节点
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer oriFrameNumber = nodeData.getInteger("oriFrameNumber");//获取ORI的帧数
        Integer accFrameNumber = nodeData.getInteger("accFrameNumber");//获取ACC的帧数

        if (oriFrameNumber!=null){
            long beforeFrameORI = PublicVariable.beforeFrameORIS[number-1];//获取上一帧的ORI帧号
            PublicVariable.beforeFrameORIS[number-1] = oriFrameNumber;//将当前帧覆盖在对应节点ORI帧号
            if (oriFrameNumber-beforeFrameORI>1) {//如果当前帧号和上一帧帧号相差超过一
                File file = new File("./LostFrameLog" + LocalDate.now() + ".txt");//创建一个记录丢失帧的文件
                FileWriter writer;
                if (!file.exists()) {
                    writer = new FileWriter(file);
                    file.createNewFile();
                } else {
                    writer = new FileWriter(file, true);
                }
                for (long i = 0; i < oriFrameNumber - beforeFrameORI - 1; i++) {//将当前帧和上一帧之间的帧号记录在文件当中
                    long lostFrameORI = i + beforeFrameORI;
                    lostFrameORI++;
                    writer.write(spf.format(System.currentTimeMillis())+"  节点:" + number +" 丢失四元素帧号: "+ lostFrameORI + "\r\n");
                }
                writer.write(spf.format(System.currentTimeMillis())+"  节点:" + number +" 丢失四元素帧数: "+ (oriFrameNumber - beforeFrameORI - 1) + "\r\n");//记录共丢失了多少帧
                writer.write("\r\n");
                writer.flush();
                writer.close();
            }
        }

        //ACC记录丢失帧同上
        if (accFrameNumber!=null){
            long beforeFrameACC = PublicVariable.beforeFrameACCS[number-1];
            PublicVariable.beforeFrameACCS[number-1] = accFrameNumber;
            if (accFrameNumber-beforeFrameACC>1) {
                File file = new File("./LostFrameLog" + LocalDate.now() + ".txt");
                FileWriter writer;
                if (!file.exists()) {
                    writer = new FileWriter(file);
                    file.createNewFile();
                } else {
                    writer = new FileWriter(file, true);
                }
                for (long i = 0; i < accFrameNumber - beforeFrameACC - 1; i++) {
                    long lostFrameACC = i + beforeFrameACC;
                    lostFrameACC++;
                    writer.write(spf.format(System.currentTimeMillis())+"  节点:" + number +" 丢失加速度帧号: "+ lostFrameACC + "\r\n");
                }
                writer.write(spf.format(System.currentTimeMillis())+"  节点:" + number +" 丢失加速度帧数: "+ (accFrameNumber - beforeFrameACC - 1) + "\r\n");//记录共丢失了多少帧
                writer.write("\r\n");
                writer.flush();
                writer.close();
            }
        }

    }


    //解析错误数据记录
    public static void errorDataLogFirm(List<Byte> data) throws IOException {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File("./errorDataLog"+LocalDate.now()+".txt");
        FileWriter writer;
        if (!file.exists()){
            writer = new FileWriter(file);
            file.createNewFile();
        }else {
            writer = new FileWriter(file, true);
        }
        writer.write(spf.format(System.currentTimeMillis())+"  错误数据："+data+"\r\n");
        writer.write("\r\n");
        writer.flush();
        writer.close();
    }

    //解析算法错误数据记录
    public static void errorDataLogALG(List<Byte> data) throws IOException {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File("./errorDataLog"+LocalDate.now()+".txt");
        FileWriter writer;
        if (!file.exists()){
            writer = new FileWriter(file);
            file.createNewFile();
        }else {
            writer = new FileWriter(file, true);
        }
        writer.write(spf.format(System.currentTimeMillis())+"  错误数据："+data+"\r\n");
        writer.write("\r\n");
        writer.flush();
        writer.close();
    }


    //传感器通信耗时记录
    public static void communicationTimeLog(int number , int time) throws IOException {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        File file = new File("./communicationTimeLog"+LocalDate.now()+".txt");
        FileWriter writer;
        if (!file.exists()){
            writer = new FileWriter(file);
            file.createNewFile();
        }else {
            writer = new FileWriter(file, true);
        }
        writer.write(spf.format(System.currentTimeMillis())+"  节点"+number+"完成一次通信  耗时："+time+"ms  \r\n");
        writer.write("\r\n");
        writer.flush();
        writer.close();
    }

    //打印控制台日志
    public static void logOut() throws IOException {
        File f=new File("./outLog"+ LocalDate.now() +".txt");
        f.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
    }
}
