package com.huantek.vein.socket;

import com.huantek.vein.util.PublicVariable;
import com.huantek.vein.util.TransformUtil;
import org.jctools.maps.NonBlockingHashMap;

import java.util.List;
import java.util.Queue;

public class DataProcessingHandy2 {


    public static void actionDataHandy(List<Integer> pfd, Integer number) {
        if (number == 18 || number == 19) {
            List<NonBlockingHashMap<Integer, double[]>> dataHashMapsORI = PublicVariable.dataHashMapsORI;
            List<Integer> firstFinger, middleFinger, ringFinger, littleFinger, pollexTop, pollexEnd, hand , frameDate;
            frameDate = pfd.subList(3, 7);
            firstFinger = pfd.subList(7, 23);
            middleFinger = pfd.subList(23, 39);
            ringFinger = pfd.subList(39, 55);
            littleFinger = pfd.subList(55, 71);
            pollexTop = pfd.subList(71, 87);
            pollexEnd = pfd.subList(87, 103);
            hand = pfd.subList(103, 119);
            int frame = TransformUtil.bytesToIntsmallHandy2(TransformUtil.ListIntToByte(frameDate), 0);
            double[] firstFingerArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(firstFinger.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(firstFinger.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(firstFinger.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(firstFinger.subList(12, 16)), 0)};
            double[] middleFingerArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(middleFinger.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(middleFinger.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(middleFinger.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(middleFinger.subList(12, 16)), 0)};
            double[] ringFingerArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(ringFinger.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(ringFinger.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(ringFinger.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(ringFinger.subList(12, 16)), 0)};
            double[] littleFingerArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(littleFinger.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(littleFinger.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(littleFinger.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(littleFinger.subList(12, 16)), 0)};
            double[] pollexTopArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexTop.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexTop.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexTop.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexTop.subList(12, 16)), 0)};
            double[] pollexEndArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexEnd.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexEnd.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexEnd.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(pollexEnd.subList(12, 16)), 0)};
            double[] handArray = {TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(hand.subList(0, 4)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(hand.subList(4, 8)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(hand.subList(8, 12)), 0),
                    TransformUtil.byte2floatParamInt(TransformUtil.ListIntToByte(hand.subList(12, 16)), 0)};
            //String[] xx = {Arrays.toString(firstFingerArray),Arrays.toString(middleFingerArray),Arrays.toString(ringFingerArray),Arrays.toString(littleFingerArray),Arrays.toString(pollexTopArray),Arrays.toString(pollexEndArray),Arrays.toString(handArray)};
            //System.out.println(Arrays.toString(xx));
            if (number == 18) {
                dataHashMapsORI.get(number - 1).put(frame,firstFingerArray);

                dataHashMapsORI.get(number).put(frame,middleFingerArray);

                dataHashMapsORI.get(number + 1).put(frame,ringFingerArray);

                dataHashMapsORI.get(number + 2).put(frame,littleFingerArray);

                dataHashMapsORI.get(number + 4).put(frame,pollexTopArray);//

                dataHashMapsORI.get(number + 3).put(frame,pollexEndArray);//算法需要顺序是先指尾后指尖，所以顺序交换

                dataHashMapsORI.get(6).put(frame,handArray);//替换手部
            } else if (number == 19) {
                dataHashMapsORI.get(number + 4).put(frame,firstFingerArray);

                dataHashMapsORI.get(number + 5).put(frame,middleFingerArray);

                dataHashMapsORI.get(number + 6).put(frame,ringFingerArray);

                dataHashMapsORI.get(number + 7).put(frame,littleFingerArray);

                dataHashMapsORI.get(number + 9).put(frame,pollexTopArray);//

                dataHashMapsORI.get(number + 8).put(frame,pollexEndArray);

                dataHashMapsORI.get(13).put(frame,handArray);//替换手部
            }
        }
    }


}
