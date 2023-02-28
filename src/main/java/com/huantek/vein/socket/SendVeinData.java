package com.huantek.vein.socket;

import com.huantek.vein.util.PublicVariable;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


//定时发送Vein数据给算法，10毫秒发送一次最新VeinData不管VeinData中有多少数据
public class SendVeinData extends Thread{

    private ConcurrentHashMap<String,Socket> socketMap;
    private OutputStream outputStream = null;
    public static int sendFrameCountALG = 0 , count = 0;
    final long time = 15;
    public SendVeinData(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    static byte[] head = "??".getBytes();
    static byte[] tail = "!!".getBytes();

    @SneakyThrows
    @Override
    public void run(){
        Socket U3D = socketMap.get("U3D");//获取算法的socket
        while (true){
            try {
                synchronized (this){
                    if (!Thread.currentThread().isInterrupted()){
                        if(U3D != null) {
                            outputStream = U3D.getOutputStream();//获取算法的输出流
                            //判断vein的数据个数是否满足节点的个数
                            //integratedData.count记录当前连接的固件节点的总个数
                            //if中的条件时判断第一帧过后Vein的长度会+1，增加一个帧数字段
                            if (PublicVariable.VeinData!=null && PublicVariable.VeinData.size()>0){

                                String s1 = String.valueOf(PublicVariable.VeinData);//json转string
                                byte[] sumData = s1.getBytes();//string转byte[]
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                                stream.write(head,0,head.length);
                                stream.write(sumData,0,sumData.length);
                                stream.write(tail,0,tail.length);
                                byte[] bytes = stream.toByteArray();
                                outputStream.write(bytes);
                                outputStream.flush();
                            }
                        }
                        SendVeinData.sleep(time);
                    }else {
                        this.interrupt();
                        break;
                    }
                }
            }catch (Exception e){
                if (outputStream!=null) outputStream.close();
                if (U3D!=null) U3D.close();
                PublicVariable.U3DThread = false;
                this.interrupt();
            }
        }

    }


    //int值转4字节数组
    private static byte[] chai(int n) {
        // 新建四个长度的byte数组
        byte[] arr = new byte[4];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) (n>>8*(arr.length-i-1));
        }
        return arr;
    }
}
