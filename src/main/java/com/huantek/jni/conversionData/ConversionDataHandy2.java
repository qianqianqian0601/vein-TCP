
package com.huantek.jni.conversionData;

import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.Service.ServiceImpl.BluetoothSwitch;
import com.huantek.vein.socket.QueueDataToList;
import com.huantek.vein.socket.SocketServer;
import com.huantek.vein.util.PublicVariable;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class ConversionDataHandy2 extends Thread{

    private double[][] oriHandy2 = PublicVariable.oriHandy2();
    public static int handy2Pose = 0;
    static double[] dstPostureTPose = new double[32 * 4];
    static double[] dstPostureTPose2 = new double[32 * 4];
    private static String[] deltaNumHandy2 = {
            "leftHand",
            "leftForeFingerUnder" , "leftForeFingerMid" , "leftForeFingerUp" ,
            "leftMiddleFingerUnder" , "leftMiddleFingerMid" , "leftMiddleFingerUp" ,
            "leftRingFingerUnder" , "leftRingFingerMid" , "leftRingFingerUp" ,
            "leftLittleFingerUnder" , "leftLittleFingerMid" , "leftLittleFingerUp" ,
            "leftThumbUnder" , "leftThumbMid" , "leftThumbUp" ,

            "rightHand",
            "rightForeFingerUnder" , "rightForeFingerMid" , "rightForeFingerUp" ,
            "rightMiddleFingerUnder" , "rightMiddleFingerMid" , "rightMiddleFingerUp" ,
            "rightRingFingerUnder" , "rightRingFingerMid" , "rightRingFingerUp" ,
            "rightLittleFingerUnder" , "rightLittleFingerMid" , "rightLittleFingerUp" ,
            "rightThumbUnder" , "rightThumbMid" , "rightThumbUp"
    };
    final int [] subscripts = {6,17,18,19,20,21,22,13,23,24,25,26,27,28};
    int count = 0;
    SocketIOServer server;
    public static declareFuncHandy handyTmp = new declareFuncHandy();
    public ConversionDataHandy2(SocketIOServer server) {
        this.server = server;
    }

    public static long time = 20000000;//纳秒
    long timeMillisOne, timeMillisTwo;
    @Override
    public void run() {
        double[] oriHandy = new double[14*4];
        QueueDataToList queueDataToList = new QueueDataToList();
        while (SocketServer.softwareFlag.equals("Handy2")){
            try {
                if (KeyFrameInterpolation.dataUseFlag==true || PublicVariable.bluetoothFlag==true) {
                    if (ConversionData.mark == 0) {
                        timeMillisOne = System.nanoTime();
                    }
                    List<Integer> indexList = queueDataToList.removeDuplicate(PublicVariable.connectingList);
                    for (Integer index : indexList) {
                        int site = -1;
                        for (int i = 0; i < subscripts.length; i++) {
                            if (index - 1 == subscripts[i]) {
                                site = i;
                            }
                        }
                        NonBlockingHashMap<Integer, double[]> mapORI = PublicVariable.dataHashMapsORI.get(index - 1);
                        if (!mapORI.isEmpty()) {
                            double[] oriData = mapORI.get(ConversionData.mark);
                            if (oriData != null && site != -1) {
                                oriHandy2[site][0] = oriData[0];
                                oriHandy2[site][1] = oriData[1];
                                oriHandy2[site][2] = oriData[2];
                                oriHandy2[site][3] = oriData[3];
                            }
                        }
                        Iterator<Integer> iterator = mapORI.keySet().iterator();
                        while (iterator.hasNext()) {
                            Integer key = iterator.next();
                            if (key <= ConversionData.mark) {
                                iterator.remove();
                            }
                        }
                        oriHandy = twoToneArray(oriHandy2);
                    }
                    ConversionData.mark++;
                    if (PublicVariable.handyFlagOne == true) {//转换数据 就是这
                        count++;//记录帧数
                        handyTmp.update(oriHandy, dstPostureTPose, dstPostureTPose2);//ori:算法所需要四元素,acc:算法所需要加速度,算法转换后放入储存容器

                        for (int i = 0; i < 32; i++) {
                            double[] value = {dstPostureTPose[i * 4 + 0], dstPostureTPose[i * 4 + 1], dstPostureTPose[i * 4 + 2], dstPostureTPose[i * 4 + 3]};
                            PublicVariable.VeinData.put(deltaNumHandy2[i], value);
                        }
                        PublicVariable.VeinData.put("frame", count);
                        PublicVariable.VeinData.put("control", "veinData");
                        server.getBroadcastOperations().sendEvent("msgInfo", PublicVariable.VeinData);//通过webSocket发送给前端\
                        customSleep(2);

                        count++;//记录帧数
                        for (int i = 0; i < 32; i++) {
                            double[] value = {dstPostureTPose2[i * 4 + 0], dstPostureTPose2[i * 4 + 1], dstPostureTPose2[i * 4 + 2], dstPostureTPose2[i * 4 + 3]};
                            PublicVariable.VeinData.put(deltaNumHandy2[i], value);
                        }
                        PublicVariable.VeinData.put("frame", count);
                        PublicVariable.VeinData.put("control", "veinData");
                        server.getBroadcastOperations().sendEvent("msgInfo", PublicVariable.VeinData);//通过webSocket发送给前端
                        customSleep(1);
                    } else {//校准111
                        if (handy2Pose == 0) {
                            for (int i = 7; i < subscripts.length; i++) {
                                oriHandy[i * 4 + 0] = 1.00;
                                oriHandy[i * 4 + 1] = 0.00;
                                oriHandy[i * 4 + 2] = 0.00;
                                oriHandy[i * 4 + 3] = 0.00;
                            }
                        } else {
                            for (int i = 0; i < 7; i++) {
                                oriHandy[i * 4 + 0] = 1.00;
                                oriHandy[i * 4 + 1] = 0.00;
                                oriHandy[i * 4 + 2] = 0.00;
                                oriHandy[i * 4 + 3] = 0.00;
                            }
                        }
                        handyTmp.calibration(handy2Pose, oriHandy, dstPostureTPose);//ori:算法所需要四元素,acc:算法所需要加速度,算法转换后放入储存容器
                        for (int i = 0; i < 32; i++) {
                            double[] value = {dstPostureTPose[i * 4 + 0], dstPostureTPose[i * 4 + 1], dstPostureTPose[i * 4 + 2], dstPostureTPose[i * 4 + 3]};
                            PublicVariable.VeinData.put(deltaNumHandy2[i], value);
                        }
                        PublicVariable.VeinData.put("control", "veinData");
                        server.getBroadcastOperations().sendEvent("msgInfo", PublicVariable.VeinData);//通过webSocket发送给前端
                        System.out.println("校准了！！！！！！！！！！");
                        customSleep(1);
                        PublicVariable.handyFlagOne = true;//校准完成继续循环
                    }
                    PublicVariable.bluetoothFlag = false;
                    timeMillisOne = timeMillisOne + time;//上一次循环时间+固定阻塞时间=本次循环时间
                }else {
                    sleep(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 二维数组扁平化一维数组
     * @param params
     * @return
     */

    public double[] twoToneArray(double[][] params){
        double[] doubles;
        int len = 0,index = 0;
        for (double[] param : params) {
            len += param.length;
        }
        doubles = new double[len];
        for (double[] param : params) {
            for (double value : param) {
                doubles[index] = value;
                index++;
            }
        }
        return doubles;
    }


    //自定义阻塞
    //divisor 除数
    public void customSleep(int divisor) {
        while (true) {
            timeMillisTwo = System.nanoTime();
            long l = (timeMillisTwo - timeMillisOne);
            if (l >= time / divisor) {
                break;
            }
        }
    }
}

