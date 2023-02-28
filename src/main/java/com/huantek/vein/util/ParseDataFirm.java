package com.huantek.vein.util;

import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.Model.FirmSuit;
import com.huantek.vein.socket.SocketServer;
import com.huantek.vein.socket.SocketThreadFirm;
import org.apache.commons.lang3.ArrayUtils;
import org.jctools.maps.NonBlockingHashMap;

import java.util.*;

public class ParseDataFirm {

    //接收本线程相关数据
    public  List<Byte> byteData = null;

    public ParseDataFirm(List<Byte> pfd) {
        this.byteData = pfd;
    }

    private static String mac = "";
    private static long times , countNumber;
    public static int [] i = {
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0
    };
    public static int [] j = {
            0,0,0,0,0,
            0,0,0,0,0,
            0,0,0,0,0,
            0,0
    };


    public void  returnParseData(JSONObject nodeData, Integer number, Integer nodeIndex){
        try {

            Byte controlFieldData = byteData.get(1);//控制域数据

            if (controlFieldData==0x04){//设备列表查询
                int productID = bytesToIntsmall(ListToByte(byteData.subList(3, 7)), 0);//产品ID
                int sensorNumber = byteData.get(7);//传感器序号
                mac = bytesToHex(ListToByte(byteData.subList(8, 14)));
                float hardwareVersion = byte2float(ListToByte(byteData.subList(14, 18)), 0);//硬件版本
                float firmWareVersion = byte2float(ListToByte(byteData.subList(18, 22)), 0);//固件版本
                int pickUpLevel = byteData.get(22);//校准等级
                nodeData.put("productID",productID);
                nodeData.put("sensorNumber",sensorNumber);
                nodeData.put("MAC",mac);
                nodeData.put("hardwareVersion",hardwareVersion);
                nodeData.put("firmWareVersion",firmWareVersion);
                nodeData.put("pickUpLevel",pickUpLevel);
                nodeData.put("control","deviceInfo");
            }

            if (controlFieldData == 0x03){   //电量信息查询
                int cellPrice = byteData.get(3) & 0xff;
                int cellEvent = byteData.get(4) & 0xff;
                nodeData.put("cellPrice",cellPrice);
                nodeData.put("cellEvent",cellEvent);
                nodeData.put("MAC",mac);
                nodeData.put("control","queryCellInfo");
            }

            if (controlFieldData == 0x12){   //电量信息查询
                if (byteData.get(3)==0x01) nodeData.put("pickUpStatus","PickUpCalibration-Start");
                if (byteData.get(3)==0x02){
                    nodeData.put("pickUpStatus","PickUpCalibration-End");
                    if (byteData.get(4)==0x01) nodeData.put("pickUpLevel",1);
                    if (byteData.get(4)==0x02) nodeData.put("pickUpLevel",2);
                    if (byteData.get(4)==0x03) nodeData.put("pickUpLevel",3);
                }
                if (byteData.get(3)==0x03){
                    if (byteData.get(4)==0x01) nodeData.put("pickUpStatus","PickUpCalibration-Succeed");
                    if (byteData.get(4)==0xe1) nodeData.put("pickUpStatus","PickUpCalibration-Fail");
                    if (byteData.get(4)==0xe2) nodeData.put("pickUpStatus","PickUpCalibration-OverTime");
                }
                nodeData.put("MAC",mac);
                nodeData.put("control","queryCellInfo");
            }

            if (controlFieldData == 0x0A){  //设备信号查询
                int signal = byteData.get(3);
                nodeData.put("signal",signal);
                nodeData.put("MAC",mac);
                nodeData.put("control","querySignal");
            }

            if (controlFieldData == 0x10){  //设备帧率
                int FPS = byteData.get(3);
                nodeData.put("FPS",FPS);
                nodeData.put("MAC",mac);
                nodeData.put("control","queryFPS");
            }

            if (controlFieldData == 0x05){//固件升级返回信息
                Byte status = byteData.get(3);
                if (status == 0x01){
                    nodeData.put("MAC",mac);
                    nodeData.put("updateStatus",0);
                }
                if (status == 0){
                    nodeData.put("MAC",mac);
                    nodeData.put("updateStatus",1);
                }
                nodeData.put("control","FirmVersionUp");
            }

            if (controlFieldData == 0x08){//节点配置返回信息
                Byte status = byteData.get(3);
                if (status == 0x01){
                    nodeData.put("MAC",mac);
                    nodeData.put("updateStatus",0);
                }
                if (status == 0){
                    nodeData.put("MAC",mac);
                    nodeData.put("updateStatus",1);
                }
                nodeData.put("control","firmwareSetting");
                nodeData.put("action","nodeSetUP");
            }

            if (controlFieldData == 0x09){//产品ID配置返回信息
                Byte status = byteData.get(3);
                if (status == 0x01){
                    nodeData.put("MAC",mac);
                    nodeData.put("updateStatus",0);
                }
                if (status == 0){
                    nodeData.put("MAC",mac);
                    nodeData.put("updateStatus",1);
                }
                nodeData.put("control","firmwareSetting");
                nodeData.put("action","productIDSetUP");
            }


            if (controlFieldData==0x01&&number!=null){//处理handy的数据
                if (number == 18 || number == 19) {
                    List<NonBlockingHashMap<Integer, double[]>> dataHashMapsORI = PublicVariable.dataHashMapsORI;
                    List<Byte> firstFinger, middleFinger, ringFinger, littleFinger, pollexTop, pollexEnd, hand , frameData;
                    frameData = byteData.subList(3, 7);
                    firstFinger = byteData.subList(7, 23);
                    middleFinger = byteData.subList(23, 39);
                    ringFinger = byteData.subList(39, 55);
                    littleFinger = byteData.subList(55, 71);
                    pollexTop = byteData.subList(71, 87);
                    pollexEnd = byteData.subList(87, 103);
                    hand = byteData.subList(103, 119);
                    int frame = TransformUtil.bytesToIntsmall(TransformUtil.ListByteToByte(frameData), 0);
                    double[] firstFingerArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(firstFinger.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(firstFinger.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(firstFinger.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(firstFinger.subList(12, 16)), 0)};
                    double[] firstFingerJoinTemp = handy2DataJoinTemp(firstFingerArray);

                    double[] middleFingerArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(middleFinger.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(middleFinger.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(middleFinger.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(middleFinger.subList(12, 16)), 0)};
                    double[] middleFingerJoinTemp = handy2DataJoinTemp(middleFingerArray);

                    double[] ringFingerArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(ringFinger.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(ringFinger.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(ringFinger.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(ringFinger.subList(12, 16)), 0)};
                    double[] ringFingerJoinTemp = handy2DataJoinTemp(ringFingerArray);

                    double[] littleFingerArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(littleFinger.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(littleFinger.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(littleFinger.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(littleFinger.subList(12, 16)), 0)};
                    double[] littleFingerJoinTemp = handy2DataJoinTemp(littleFingerArray);

                    double[] pollexTopArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexTop.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexTop.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexTop.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexTop.subList(12, 16)), 0)};
                    double[] pollexTopJoinTemp = handy2DataJoinTemp(pollexTopArray);

                    double[] pollexEndArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexEnd.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexEnd.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexEnd.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(pollexEnd.subList(12, 16)), 0)};
                    double[] pollexEndJoinTemp = handy2DataJoinTemp(pollexEndArray);

                    double[] handArray = {TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(hand.subList(0, 4)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(hand.subList(4, 8)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(hand.subList(8, 12)), 0),
                            TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(hand.subList(12, 16)), 0)};
                    double[] handJoinTemp = handy2DataJoinTemp(handArray);

                    //String[] xx = {Arrays.toString(firstFingerArray),Arrays.toString(middleFingerArray),Arrays.toString(ringFingerArray),Arrays.toString(littleFingerArray),Arrays.toString(pollexTopArray),Arrays.toString(pollexEndArray),Arrays.toString(handArray)};
                    //System.out.println(Arrays.toString(xx));
                    if (number == 18) {
                        dataHashMapsORI.get(number - 1).put(frame , firstFingerJoinTemp);

                        dataHashMapsORI.get(number).put(frame , middleFingerJoinTemp);

                        dataHashMapsORI.get(number + 1).put(frame , ringFingerJoinTemp);

                        dataHashMapsORI.get(number + 2).put(frame , littleFingerJoinTemp);

                        dataHashMapsORI.get(number + 4).put(frame , pollexTopJoinTemp);//

                        dataHashMapsORI.get(number + 3).put(frame , pollexEndJoinTemp);//算法需要顺序是先指尾后指尖，所以顺序交换

                        dataHashMapsORI.get(6).put(frame,handJoinTemp);//替换手部
                    } else if (number == 19) {
                        dataHashMapsORI.get(number + 4).put(frame , firstFingerJoinTemp);

                        dataHashMapsORI.get(number + 5).put(frame , middleFingerJoinTemp);

                        dataHashMapsORI.get(number + 6).put(frame , ringFingerJoinTemp);

                        dataHashMapsORI.get(number + 7).put(frame , littleFingerJoinTemp);

                        dataHashMapsORI.get(number + 9).put(frame , pollexTopJoinTemp);//

                        dataHashMapsORI.get(number + 8).put(frame , pollexEndJoinTemp);//算法需要顺序是先指尾后指尖，所以顺序交换

                        dataHashMapsORI.get(13).put(frame , handJoinTemp);//替换手部
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * list<byte>转Byte[]
     * @param list
     * @return
     */
    public static byte[] ListToByte(List<Byte> list){
        if (list == null || list.size()<0) return null;

        byte [] arr = new byte[list.size()];
        Iterator<Byte> iterator = list.iterator();
        int i = 0;
        while (iterator.hasNext()){
            arr[i] = iterator.next();
            i++;
        }
        return arr;
    }


    /**
     * handy2数据没有加速度，所以需要拼接一个空的加速度
     * @param handy2Data
     * @return
     */
    public static double[] handy2DataJoinTemp(double[] handy2Data){
        double[] temp = {0 , 0 , 0};
        double[] bytes = ArrayUtils.addAll(handy2Data, temp);
        return bytes;
    }




    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序-小端序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToIntsmall(byte[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24);
        return value;
    }


    //这个函数将byte转换成float
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }


    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
