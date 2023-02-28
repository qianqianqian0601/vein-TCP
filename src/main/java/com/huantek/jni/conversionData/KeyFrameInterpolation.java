package com.huantek.jni.conversionData;

import com.huantek.vein.socket.QueueDataToList;
import com.huantek.vein.util.PublicVariable;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

//补帧
@Slf4j
public class KeyFrameInterpolation {

    public static Timer insertFramesTimer = null;
    public static boolean dataUseFlag = false;//map数据可用标志位

    public void insertFrameEvent() {
        insertFramesTimer = new Timer();
        insertFramesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Integer> list = new QueueDataToList().removeDuplicate(PublicVariable.connectingList);
                if (!list.isEmpty()){
                    doInsertFrames(list);
                    dataUseFlag=true;
                }
            }
        }, 300, 100);
    }


    public void doInsertFrames(List<Integer> list){
        for (Integer num : list) {
            NonBlockingHashMap<Integer, double[]> map = PublicVariable.dataHashMapsORI.get(num - 1);
            //log.warn("帧数："+map.size()+"  节点号："+num);
                    NonBlockingHashMap<Integer, double[]> copyMap = new NonBlockingHashMap<>();
                    copyMap.putAll(map);
            //List<Integer> integers = Arrays.asList(map.keySet().toArray(new Integer[map.keySet().size()]));
            List<Integer> integers = copyMap.keySet().stream().filter(a -> a != null).sorted().collect(Collectors.toList());
            for (int i = 0; i < integers.size() - 1; i++) {
                Integer a = integers.get(i + 1);//后一帧
                Integer b = integers.get(i);//前一帧
                int c = a - b;
                double[] doublesB = map.get(b);//前一帧数据
                double[] doublesA = map.get(a);//后一帧数据
                if (c != 1) {//差值不等于1，缺帧
                    //System.out.println("前一帧： "+Arrays.toString(doublesB)+"  帧号:"+b+ "   节点："+num);
                    //System.out.println("后一帧： "+Arrays.toString(doublesA)+"  帧号:"+a+ "   节点："+num);
                    //System.out.println("差值： "+c +"mark："+ConversionData.mark);
                    for (int j = 1; j < c; j++) {//循环差值
                        int frameNum = b + j;//计算缺值帧号
                        double[] doublesOriC = new double[4];
                        if (doublesA != null && doublesB != null) {
                            double[] doublesOriB = new double[4];
                            doublesOriB[0] = doublesB[0];
                            doublesOriB[1] = doublesB[1];
                            doublesOriB[2] = doublesB[2];
                            doublesOriB[3] = doublesB[3];
                            double[] doublesAccB = new double[3];
                            doublesAccB[0] = doublesB[4];
                            doublesAccB[1] = doublesB[5];
                            doublesAccB[2] = doublesB[6];
                            double[] doublesOriA = new double[4];
                            doublesOriA[0] = doublesA[0];
                            doublesOriA[1] = doublesA[1];
                            doublesOriA[2] = doublesA[2];
                            doublesOriA[3] = doublesA[3];
                            double[] doublesAccA = new double[3];
                            doublesAccA[0] = doublesA[4];
                            doublesAccA[1] = doublesA[5];
                            doublesAccA[2] = doublesA[6];
                            try {
                                QuatSlerp.QLibray.INSTANCE.slerp(doublesOriB, doublesOriA, (double) j / c, doublesOriC);//调用前后帧补帧
                            }catch (Exception e){
                                e.printStackTrace();
                                log.info("补帧报错！！！！");
                            }
                            double[] doublesAccC = insertFrameAcc(doublesAccB, doublesAccA, j, c);
                            double[] doublesC = new double[7];
                            doublesC[0] = doublesOriC[0];
                            doublesC[1] = doublesOriC[1];
                            doublesC[2] = doublesOriC[2];
                            doublesC[3] = doublesOriC[3];
                            doublesC[0] = doublesAccC[0];
                            doublesC[1] = doublesAccC[1];
                            doublesC[2] = doublesAccC[2];
                            map.put(frameNum, doublesC);
                        } else {
                            log.debug("null值A帧号：" + a + "   null值B帧号：" + b + "现用至帧号：" + ConversionData.mark);
                        }
                    }
                }
            }
        }
    }

    /**
     * 加速度补帧
     * @param doublesAccB 前一帧
     * @param doublesAccA 后一帧
     * @param j 补帧的第几帧
     * @param c 相差帧
     * @return
     */
    private double[] insertFrameAcc(double[] doublesAccB, double[] doublesAccA, int j, int c) {
        double[] doublesAccC = new double[3];
        doublesAccC[0] = doublesAccB[0] * (c - j) / c + doublesAccA[0] * j / c;
        doublesAccC[1] = doublesAccB[1] * (c - j) / c + doublesAccA[1] * j / c;
        doublesAccC[2] = doublesAccB[2] * (c - j) / c + doublesAccA[2] * j / c;
        return doublesAccC;
    }


    public void insertFramesCancel() {
        insertFramesTimer.cancel();
        dataUseFlag = false;
        ConversionData.mark = 0;
        PublicVariable.dataHashMapsORI.clear();
        PublicVariable.dataHashMapsORI = PublicVariable.initListMapORI();
    }
}
