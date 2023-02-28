package com.huantek.vein.util;

import com.alibaba.fastjson.JSONObject;
import com.huantek.jni.conversionData.KeyFrameInterpolation;
import com.huantek.vein.Model.FirmNode;
import org.jctools.maps.NonBlockingHashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class PublicVariable {
    public static ConcurrentHashMap<String, Socket> socketMap = new ConcurrentHashMap<>();//公共的socket集合
    public static KeyFrameInterpolation keyFrameInterpolation;
    public static boolean threadFlagOne = true;//校准标志位
    public static boolean handyFlagOne = true;//校准标志位
    public static boolean threadFlagTwo = false;//串流标志位
    public static boolean U3DThread = false;//U3D标志位
    public static String recordThread = "stop";//日志标志位
    public static String calibrationFlag = "";
    public static long newConnectTime;
    public static boolean connectFlag = false;
    public static int meteringStart = 1;
    public static int meteringEnd = 1;
    public static long[] startTimes = {0, 0};
    public static long[] endTimes = {0, 0};
    public static boolean veins = false;
    public static boolean handy2 = false;
    public static boolean bluetoothFlag = false;

    /**
     * 四元素公共变量
     *
     * @return
     */
    public static double[][] ori() {
        return oriOrOriHandy2(29, 4);
    }

    /**
     * 四元素公共变量handy2
     *
     * @return
     */
    public static double[][] oriHandy2() {
        return oriOrOriHandy2(14, 4);
    }

    public static double[][] oriOrOriHandy2(int one, int two) {
        double[][] ori = new double[one][two];
        for (int i = 0; i < ori.length; i++) {
            ori[i][0] = 1.00;
            ori[i][1] = 0;
            ori[i][2] = 0;
            ori[i][3] = 0;
        }
        return ori;
    }



    public static double[][] ori = ori();

    public static double[][] acc = new double[29][3]; //加速度公共变量

    public static JSONObject VeinData = new JSONObject();//动作数据

    public static HashMap<String, FirmNode> handyFirmMap = new HashMap<>();

    public static int recordFrameCount = 0;

    public static List<List<Long>> sendTimeByNode =  initSendTimeList();

    public static ArrayList<List<Long>> initSendTimeList(){
        List<Long> sendTime = new ArrayList<>();//发送固件信息时间集合
        ArrayList<List<Long>> lists = new ArrayList<>();
        for (int i = 0; i <= 17; i++) {
            lists.add(sendTime);
        }
        return lists;
    }

    public static List<Long> sendTimeALG = new ArrayList<>();//发送算法信息时间集合

    public static long[] beforeFrameORIS = {//ORI帧数记录
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0
    };

    public static long[] beforeFrameACCS= {//ACC帧数记录
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0
    };

    public static int [] VeinDataCountACCS = {
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0
    };

    public static int [] VeinDataCountORIS = {
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0
    };

    public static List<NonBlockingHashMap<Integer, double[]>> dataHashMapsORI = initListMapORI();

    /**
     * 构造一个17个队列的四元素数组
     *
     * @return
     */
    public static List<NonBlockingHashMap<Integer, double[]>> initListMapORI() {
        return autoList(29);
    }

    private static List<NonBlockingHashMap<Integer, double[]>> autoList(int size) {
        List<NonBlockingHashMap<Integer, double[]>> hashMaps = new ArrayList();
        for (int i = 0; i < size; i++) {
            NonBlockingHashMap<Integer, double[]> node = new NonBlockingHashMap<>();
            hashMaps.add(i, node);
        }
        return hashMaps;
    }

    public static List<Queue> queueListORI(){//构造一个17个队列的数组
        List<Queue> queue = new ArrayList();
        Queue<Double[]> node1 = new LinkedList();
        Queue<Double[]> node2 = new LinkedList();
        Queue<Double[]> node3 = new LinkedList();
        Queue<Double[]> node4 = new LinkedList();
        Queue<Double[]> node5 = new LinkedList();
        Queue<Double[]> node6 = new LinkedList();
        Queue<Double[]> node7 = new LinkedList();
        Queue<Double[]> node8 = new LinkedList();
        Queue<Double[]> node9 = new LinkedList();
        Queue<Double[]> node10 = new LinkedList();
        Queue<Double[]> node11 = new LinkedList();
        Queue<Double[]> node12 = new LinkedList();
        Queue<Double[]> node13 = new LinkedList();
        Queue<Double[]> node14 = new LinkedList();
        Queue<Double[]> node15 = new LinkedList();
        Queue<Double[]> node16 = new LinkedList();
        Queue<Double[]> node17 = new LinkedList();
        Queue<Double[]> node18 = new LinkedList();
        Queue<Double[]> node19 = new LinkedList();
        Queue<Double[]> node20 = new LinkedList();
        Queue<Double[]> node21 = new LinkedList();
        Queue<Double[]> node22 = new LinkedList();
        Queue<Double[]> node23 = new LinkedList();
        Queue<Double[]> node24 = new LinkedList();
        Queue<Double[]> node25 = new LinkedList();
        Queue<Double[]> node26 = new LinkedList();
        Queue<Double[]> node27 = new LinkedList();
        Queue<Double[]> node28 = new LinkedList();
        Queue<Double[]> node29 = new LinkedList();
        Queue<Double> node30 = new LinkedList();
        Queue<Double> node31 = new LinkedList();
        queue.add(0,node1);
        queue.add(1,node2);
        queue.add(2,node3);
        queue.add(3,node4);
        queue.add(4,node5);
        queue.add(5,node6);
        queue.add(6,node7);
        queue.add(7,node8);
        queue.add(8,node9);
        queue.add(9,node10);
        queue.add(10,node11);
        queue.add(11,node12);
        queue.add(12,node13);
        queue.add(13,node14);
        queue.add(14,node15);
        queue.add(15,node16);
        queue.add(16,node17);
        queue.add(17,node18);
        queue.add(18,node19);
        queue.add(19,node20);
        queue.add(20,node21);
        queue.add(21,node22);
        queue.add(22,node23);
        queue.add(23,node24);
        queue.add(24,node25);
        queue.add(25,node26);
        queue.add(26,node27);
        queue.add(27,node28);
        queue.add(28,node29);
        return queue;
    }

    public static List<Queue> queueListACC(){//构造一个17个队列的数组
        List<Queue> queue = new ArrayList();
        Queue<Double> node1 = new LinkedList();
        Queue<Double> node2 = new LinkedList();
        Queue<Double> node3 = new LinkedList();
        Queue<Double> node4 = new LinkedList();
        Queue<Double> node5 = new LinkedList();
        Queue<Double> node6 = new LinkedList();
        Queue<Double> node7 = new LinkedList();
        Queue<Double> node8 = new LinkedList();
        Queue<Double> node9 = new LinkedList();
        Queue<Double> node10 = new LinkedList();
        Queue<Double> node11 = new LinkedList();
        Queue<Double> node12 = new LinkedList();
        Queue<Double> node13 = new LinkedList();
        Queue<Double> node14 = new LinkedList();
        Queue<Double> node15 = new LinkedList();
        Queue<Double> node16 = new LinkedList();
        Queue<Double> node17 = new LinkedList();
        Queue<Double> node18 = new LinkedList();
        Queue<Double> node19 = new LinkedList();
        Queue<Double> node20 = new LinkedList();
        Queue<Double> node21 = new LinkedList();
        Queue<Double> node22 = new LinkedList();
        Queue<Double> node23 = new LinkedList();
        Queue<Double> node24 = new LinkedList();
        Queue<Double> node25 = new LinkedList();
        Queue<Double> node26 = new LinkedList();
        Queue<Double> node27 = new LinkedList();
        Queue<Double> node28 = new LinkedList();
        Queue<Double> node29 = new LinkedList();
        Queue<Double> node30 = new LinkedList();
        Queue<Double> node31 = new LinkedList();
        queue.add(0,node1);
        queue.add(1,node2);
        queue.add(2,node3);
        queue.add(3,node4);
        queue.add(4,node5);
        queue.add(5,node6);
        queue.add(6,node7);
        queue.add(7,node8);
        queue.add(8,node9);
        queue.add(9,node10);
        queue.add(10,node11);
        queue.add(11,node12);
        queue.add(12,node13);
        queue.add(13,node14);
        queue.add(14,node15);
        queue.add(15,node16);
        queue.add(16,node17);
        queue.add(17,node18);
        queue.add(18,node19);
        queue.add(19,node20);
        queue.add(20,node21);
        queue.add(21,node22);
        queue.add(22,node23);
        queue.add(23,node24);
        queue.add(24,node25);
        queue.add(25,node26);
        queue.add(26,node27);
        queue.add(27,node28);
        queue.add(28,node29);
        return queue;
    }

    public static List<Queue> dataQueuesORI = queueListORI();
    public static List<Queue> dataQueuesACC = queueListACC();


    //服务注册和注销使用
    public static HashMap<String , JmDNS> jmDNSHashMap = new HashMap<>();
    public static HashMap<JmDNS , ServiceInfo> serviceInfoHashMap = new HashMap<>();


    //记录连接节点
    public static List<Integer> connectingList = new ArrayList<>();
}
