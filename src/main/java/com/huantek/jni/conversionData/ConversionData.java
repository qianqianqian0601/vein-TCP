package com.huantek.jni.conversionData;

import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.vein.Service.ServiceImpl.BluetoothSwitch;
import com.huantek.vein.socket.QueueDataToList;
import com.huantek.vein.socket.SocketServer;
import com.huantek.vein.util.PublicVariable;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Iterator;
import java.util.List;

@Slf4j
public class ConversionData extends Thread {

    private double[][] ori = PublicVariable.ori;
    private double[][] acc = PublicVariable.acc;
    public static int pose = 0, mark = 0, number = 0, count = 0;
    public static Integer[] keepFrames = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0};
    static double[] dstPostureTPose = new double[52 * 4];
    static double[] dstPostureTPose2 = new double[52 * 4];
    private static String[] deltaNum = {"head",
            "back", "crotch",
            "leftShoulder", "leftUpperArm", "leftLowerArm", "leftHand",
            "leftUpperLeg", "leftLowerLeg", "leftFoot",
            "rightShoulder", "rightUpperArm", "rightLowerArm", "rightHand",
            "rightUpperLeg", "rightLowerLeg", "rightFoot", "waistOne", "waistTwo", "leftToe", "rightToe",
            "leftForeFingerUnder", "leftForeFingerMid", "leftForeFingerUp",
            "leftMiddleFingerUnder", "leftMiddleFingerMid", "leftMiddleFingerUp",
            "leftRingFingerUnder", "leftRingFingerMid", "leftRingFingerUp",
            "leftLittleFingerUnder", "leftLittleFingerMid", "leftLittleFingerUp",
            "leftThumbUnder", "leftThumbMid", "leftThumbUp",
            "rightForeFingerUnder", "rightForeFingerMid", "rightForeFingerUp",
            "rightMiddleFingerUnder", "rightMiddleFingerMid", "rightMiddleFingerUp",
            "rightRingFingerUnder", "rightRingFingerMid", "rightRingFingerUp",
            "rightLittleFingerUnder", "rightLittleFingerMid", "rightLittleFingerUp",
            "rightThumbUnder", "rightThumbMid", "rightThumbUp"
    };
    public static long time = 20000000;//??????
    public static boolean cbFlag = true;
    SocketIOServer server;

    public ConversionData(SocketIOServer server) {
        this.server = server;
    }

    public static declareFunc tmp = new declareFunc();
    long timeMillisOne, timeMillisTwo;
    @Override
    public void run() {
        double[] ori2 = new double[29 * 4], acc2 = new double[29 * 3];
        while (SocketServer.softwareFlag.equals("Vein")) {
            try {
                if (KeyFrameInterpolation.dataUseFlag==true) {
                    if (mark == 0) {
                        timeMillisOne = System.nanoTime();
                    }
                    QueueDataToList queueDataToList = new QueueDataToList();
                    List<Integer> indexList = queueDataToList.removeDuplicate(PublicVariable.connectingList);
                    for (Integer index : indexList) {
                        NonBlockingHashMap<Integer, double[]> mapORI = PublicVariable.dataHashMapsORI.get(index - 1);
                        if (!mapORI.isEmpty()) {
                            double[] oriData = mapORI.get(mark);
                            if (oriData != null) {
                                this.ori[index - 1][0] = oriData[0];
                                this.ori[index - 1][1] = oriData[1];
                                this.ori[index - 1][2] = oriData[2];
                                this.ori[index - 1][3] = oriData[3];
                                this.acc[index - 1][0] = oriData[4];
                                this.acc[index - 1][1] = oriData[5];
                                this.acc[index - 1][2] = oriData[6];
                            }
                        }
                        Iterator<Integer> iterator = mapORI.keySet().iterator();
                        while (iterator.hasNext()) {
                            Integer key = iterator.next();
                            if (key <= mark) {
                                iterator.remove();
                            }
                        }
                        ori2 = twoToneArray(this.ori);
                        acc2 = twoToneArray(this.acc);
                    }
                    mark++;
                    if (cbFlag) {//????????????
                        tmp.update(ori2, acc2, dstPostureTPose, dstPostureTPose2);//ori:????????????????????????,acc:????????????????????????,?????????????????????????????????
                        count++;//????????????
                        handle(dstPostureTPose, true, 2);
                        count++;//????????????
                        handle(dstPostureTPose2, true, 1);
                    } else {//??????
                        tmp.calibration(pose, ori2, dstPostureTPose);//ori:????????????????????????,acc:????????????????????????,?????????????????????????????????
                        handle(dstPostureTPose, false, 1);
                    }
                    timeMillisOne = timeMillisOne + time;//?????????????????????+??????????????????=??????????????????
                }else {
                    sleep(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param dst
     * @param hasFrame
     * @param divisor
     */
    public void handle(double[] dst, boolean hasFrame, int divisor) {
        for (int i = 0; i < 51; i++) {
            double[] value = {dst[i * 4 + 0], dst[i * 4 + 1], dst[i * 4 + 2], dst[i * 4 + 3]};
            PublicVariable.VeinData.put(deltaNum[i], value);
        }
        double[] coordinate = {dst[51 * 4 + 1], dst[51 * 4 + 2], dst[51 * 4 + 3]};//????????????
        PublicVariable.VeinData.put("coordinate", coordinate);
        if (hasFrame) {
            PublicVariable.VeinData.put("frame", count);
        } else {
            number++;
            log.info("???????????????" + number);
        }
        PublicVariable.VeinData.put("control", "veinData");
        server.getBroadcastOperations().sendEvent("msgInfo", PublicVariable.VeinData);//??????webSocket???????????????
        customSleep(divisor);
    }


    /**
     * ?????????????????????????????????
     *
     * @param params
     * @return
     */
    public double[] twoToneArray(double[][] params) {
        double[] doubles;
        int len = 0, index = 0;
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

    //???????????????
    //divisor ??????
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
