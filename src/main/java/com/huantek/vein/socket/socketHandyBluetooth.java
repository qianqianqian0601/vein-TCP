package com.huantek.vein.socket;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.jni.conversionData.ConversionDataHandy2;
import com.huantek.jni.conversionData.KeyFrameInterpolation;
import com.huantek.vein.Model.FirmNode;
import com.huantek.vein.Model.FirmSuit;
import com.huantek.vein.util.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class socketHandyBluetooth {
    //创建和本线程相关的Socket
    private SocketIOServer server = null;
    public static CopyOnWriteArrayList<FirmSuit> firmSuits = IntegratedData.initFirmSuitList();//设备套装列表初始化

    public socketHandyBluetooth(SocketIOServer socketIOServer) {
        this.server = socketIOServer;
    }

    public void bluetoothSwitchData(String deviceId, List dataPFD) throws IOException {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try {
            int  nodeIndex = 0;
            List<Integer> pfd = dataPFD;
            if (pfd != null) {

                //设备详情数据
                if (pfd.get(1) == 0x04) {
                    String productID = String.valueOf(bytesToIntsmall(ListToByte(pfd.subList(3, 7)), 0));//产品ID
                    Integer sensorNumber = pfd.get(7);//传感器序号
                    String mac = bytesToHex(ListToByte(pfd.subList(8, 14)));
                    float hardwareVersion = byte2float(ListToByte(pfd.subList(14, 18)), 0);//硬件版本
                    float firmWareVersion = byte2float(ListToByte(pfd.subList(18, 22)), 0);//固件版本
                    int pickUpLevel = pfd.get(22);//校准等级

                    if (sensorNumber == 18) {
                        productID = "handy(Left)";
                        PublicVariable.connectingList.add(7);//取的相对应的map要—1所以add()的节点号都+1//标识连接的节点号
                        PublicVariable.connectingList.add(18);
                        PublicVariable.connectingList.add(19);
                        PublicVariable.connectingList.add(20);
                        PublicVariable.connectingList.add(21);
                        PublicVariable.connectingList.add(22);
                        PublicVariable.connectingList.add(23);
                    } else if (sensorNumber == 19) {
                        productID = "handy(Right)";
                        PublicVariable.connectingList.add(14);
                        PublicVariable.connectingList.add(24);
                        PublicVariable.connectingList.add(25);
                        PublicVariable.connectingList.add(26);
                        PublicVariable.connectingList.add(27);
                        PublicVariable.connectingList.add(28);
                        PublicVariable.connectingList.add(29);
                    }else {
                        PublicVariable.connectingList.add(sensorNumber);
                    }

                    FirmNode firmNode = new FirmNode();
                    firmNode.setProductID(productID);//存入对象属性
                    firmNode.setMAC(mac);//存入对象属性
                    firmNode.setFirmVersion(firmWareVersion);
                    firmNode.setSensorNumber(sensorNumber);
                    firmNode.setPickUpLevel(pickUpLevel);


                    HashMap<String, FirmNode> handyFirmMap = PublicVariable.handyFirmMap;
                    if (handyFirmMap.containsKey(deviceId)) {
                        handyFirmMap.replace(deviceId,firmNode);
                    }else {
                        handyFirmMap.put(deviceId,firmNode);
                    }

                    FirmSuit firmSuit = new FirmSuit();
                    firmSuit.setProductID(productID);

                    int subScript = suitIsExist(productID);//判断设备列表中是否存在该产品ID的设备，true存在，false不存在
                    if (subScript == -1) {//当列表当中不存在该产品ID的设备时
                        //if (firmSuits.isEmpty()){
                        //  FirmSuit initSuit = new FirmSuit();
                        // initSuit.setProductID("");
                        //initSuit.setFirmNodes(null);
                        //firmSuits.add(initSuit);//初始化设备列表，使下次可以正常满足连接条件
                        //}
                        if (!firmSuits.isEmpty() && firmSuits.get(0).getProductID().equals("")) {//判断设备列表是不是初始化的，是初始化的覆盖在0下标上
                            firmSuits.set(0, firmSuit);
                        } else {//不是初始化的在列表数组后追加
                            firmSuits.add(firmSuit);
                        }
                    } else {
                        // firmSuits.set(subScript,firmSuit);
                        FirmSuit Suit = firmSuits.get(subScript);
                        CopyOnWriteArrayList<FirmNode> firmNodes = Suit.getFirmNodes();
                        if (sensorNumber > 0) {
                            firmNodes.set(sensorNumber - 1, firmNode);
                        } else if (sensorNumber == 0) {
                            int i = nodeIsExist(firmNodes, mac);
                            if (i == -1) {
                                Suit.getFirmNodes().add(firmNode);
                            }
                        }
                    }


                    if (PublicVariable.newConnectTime != 0) {
                        long timeMillis = System.currentTimeMillis();//当前时间
                        long l = timeMillis - PublicVariable.newConnectTime;//与上一连接相差时间
                        if (l > 500 && !PublicVariable.socketMap.isEmpty()) {//连接超过500ms并且地址不为空的发送停止命令
                            long num = PublicVariable.socketMap.values().stream().count();
                            if (num==PublicVariable.socketMap.size()) {
                                for (Socket socketModel : PublicVariable.socketMap.values()) {
                                    //发送停止指令
                                    socketModel.getOutputStream().write(OrderBase.STOP_MOTION_CAPTURE);
                                }
                                System.out.println("所有节点停止发送数据---");
                                PublicVariable.connectFlag = false;
                            }
                        }
                    }
                    PublicVariable.newConnectTime = System.currentTimeMillis();//记录新的连接时间
                }

                //数据开始和发送指令的回执
                if (pfd.get(1) == 0x14) {
                    int status = pfd.get(3);
                    long count = PublicVariable.socketMap.values().stream().count();
                    if (status == 1) {
                        if (PublicVariable.meteringStart == 1) {
                            PublicVariable.startTimes[0] = System.currentTimeMillis();
                            log.info("开始第一个： " + PublicVariable.startTimes[0]);
                        }
                        if (PublicVariable.meteringStart == count) {
                            PublicVariable.startTimes[1] = System.currentTimeMillis();
                            log.info("开始最后一个： " + PublicVariable.startTimes[1]);
                            log.info("开始相差： " + (PublicVariable.startTimes[1] - PublicVariable.startTimes[0]));
                            PublicVariable.meteringStart = 0;
                            if (null != PublicVariable.keyFrameInterpolation) {
                                PublicVariable.keyFrameInterpolation.insertFramesCancel();
                            }
                            PublicVariable.keyFrameInterpolation = new KeyFrameInterpolation();
                            PublicVariable.keyFrameInterpolation.insertFrameEvent();
                            PublicVariable.meteringStart++;
                        }
                    } else if (status == 0) {
                        if (PublicVariable.meteringEnd == 1) {
                            PublicVariable.endTimes[0] = System.currentTimeMillis();
                            log.info("停止第一个： " + PublicVariable.endTimes[0]);
                        }
                        if (PublicVariable.meteringEnd == count) {
                            PublicVariable.endTimes[1] = System.currentTimeMillis();
                            log.info("停止最后一个： " + PublicVariable.endTimes[1]);
                            log.info("停止相差： " + (PublicVariable.endTimes[1] - PublicVariable.endTimes[0]));
                            PublicVariable.meteringEnd = 0;
                        }
                        PublicVariable.meteringEnd++;
                    }
                }

                //电量信息查询
                if (pfd.get(1) == 0x03) {
                    FirmNode firmNode = PublicVariable.handyFirmMap.get(deviceId);
                    int cellPrice = pfd.get(3) & 0xff;
                    int cellEvent = pfd.get(4) & 0xff;
                    firmNode.setCellPrice(cellPrice);//存入对象属性
                }

                //传感器校准等级
                if (pfd.get(1) == 0x12) {
                    FirmNode firmNode = PublicVariable.handyFirmMap.get(deviceId);
                    JSONObject pickUpStatus = new JSONObject();
                    if (pfd.get(3) == 0x01) pickUpStatus.put("pickUpStatus", "PickUpCalibration-Start");
                    if (pfd.get(3) == 0x02) {
                        pickUpStatus.put("pickUpStatus", "PickUpCalibration-End");
                        if (pfd.get(4) == 0x01) firmNode.setPickUpLevel(1);
                        if (pfd.get(4) == 0x02) firmNode.setPickUpLevel(2);
                        if (pfd.get(4) == 0x03) firmNode.setPickUpLevel(3);
                    }
                    if (pfd.get(3) == 0x03) {
                        if (pfd.get(4) == 0x01)
                            pickUpStatus.put("pickUpStatus", "PickUpCalibration-Succeed");
                        if (pfd.get(4) == 0xe1) pickUpStatus.put("pickUpStatus", "PickUpCalibration-Fail");
                        if (pfd.get(4) == 0xe2)
                            pickUpStatus.put("pickUpStatus", "PickUpCalibration-OverTime");
                    }
                    server.getBroadcastOperations().sendEvent("msgInfo", pickUpStatus);
                }


                // handy2校准 1
                if (pfd.get(1) == 0x13 && SocketServer.softwareFlag.equals("Handy2")) {
                    PublicVariable.threadFlagOne = false;
                    int sensorNumberHandy = pfd.get(3);
                    int type = pfd.get(4);
                    if (type == 1) {//校准
                        if (sensorNumberHandy == 18) ConversionDataHandy2.handy2Pose = 0;
                        if (sensorNumberHandy == 19) ConversionDataHandy2.handy2Pose = 1;
                    }
                }

                //信号强度查询
                if (pfd.get(1) == 0x0A) {
                    int Signal = pfd.get(3);
                    FirmNode firmNode = PublicVariable.handyFirmMap.get(deviceId);
                    firmNode.setSignal(Signal);
                    String productID = firmNode.getProductID();
                    String mac = firmNode.getMAC();
                    Integer sensorNumber = firmNode.getSensorNumber();
                    for (FirmSuit suit : firmSuits) { //遍历设备列表获取单独的套装设备
                        if (suit.getProductID().equals(productID)) {//判断产品ID获取同一套设备节点信息
                            CopyOnWriteArrayList<FirmNode> firmNodes = suit.getFirmNodes();
                            //通过mac判断设备列表中同一套装下是否有该节点设备存在
                            //如果存在则返回该节点在设备列表中的下标，不存在返回-1
                            int num = nodeIsExist(firmNodes, mac);
                            nodeIndex = num;

                            if (num > -1 && num < 19) {//大于-1的话列表中存在该节点信息,小于17证明该节点有对应部位

                                if (sensorNumber > 0) {//节点信息大于0的时候
                                    if (firmNodes.get(sensorNumber - 1) == null) {//该下标目前为空就放在改下标处
                                        firmNodes.set(num, null);
                                        firmNodes.set(sensorNumber - 1, firmNode);
                                    }
                                } else if (sensorNumber == 0) {//等于0为临时节点
                                    firmNodes.set(num, null);
                                    firmNodes.add(firmNode);//临时节点追加在后面
                                }
                            } else if (num >= 19) {//大于等于17证明该节点之前是临时节点
                                if (sensorNumber > 0) {//节点改变成大于0的数,将之前临时下标的节点信息删除，并将该节点信息放在对应下标上
                                    if (firmNodes.get(sensorNumber - 1) == null) {
                                        firmNodes.remove(num);
                                        firmNodes.set(sensorNumber - 1, firmNode);
                                    } else if (firmNodes.get(sensorNumber - 1) != null && !firmNodes.get(sensorNumber - 1).equals(mac)) {
                                        continue;
                                    }
                                }//如果节点还是为0，不做任何处理
                            } else if (num == -1) {//等于-1列表不存在该节点信息
                                if (sensorNumber > 0) {//节点号不为0,则放在对应的下标上
                                    if (firmNodes.get(sensorNumber - 1) == null) {
                                        firmNodes.set(sensorNumber - 1, firmNode);
                                    } else if (firmNodes.get(sensorNumber - 1) != null && !firmNodes.get(sensorNumber - 1).getMAC().equals(mac)) {
                                        firmNodes.add(firmNode);
                                    }
                                } else if (sensorNumber == 0) {//节点号为0,则向后追加临时节点
                                    firmNodes.add(firmNode);
                                }
                            }
                        }
                    }
                    boolean isEmpty = nodesIsEmpty(firmSuits.get(0));
                    if (isEmpty) {
                        firmSuits.remove(0);
                    }
                    if (firmSuits == null) {
                        firmSuits = IntegratedData.initFirmSuitList();
                    }
                }

                //固件升级返回信息
                if (pfd.get(1) == 0x05) {
                    JSONObject updateStatus = new JSONObject();
                    Integer status = pfd.get(3);
                    if (status == 0x01) {
                        updateStatus.put("updateStatus", 0);
                    }
                    if (status == 0) {
                        updateStatus.put("updateStatus", 1);
                    }
                    updateStatus.put("control", "FirmVersionUp");
                    server.getBroadcastOperations().sendEvent("msgInfo", updateStatus);//发送给前端
                }


                //节点配置返回信息
                if (pfd.get(1) == 0x08) {
                    JSONObject nodeSetUP = new JSONObject();
                    Integer status = pfd.get(3);
                    if (status == 0x01) {
                        nodeSetUP.put("updateStatus", 0);
                    }
                    if (status == 0) {
                        nodeSetUP.put("updateStatus", 1);
                    }
                    nodeSetUP.put("control", "firmwareSetting");
                    nodeSetUP.put("action", "nodeSetUP");
                    server.getBroadcastOperations().sendEvent("msgInfo", nodeSetUP);//发送给前端
                }

                //产品ID配置返回信息
                if (pfd.get(1) == 0x09) {
                    JSONObject productIDSetUP = new JSONObject();
                    Integer status = pfd.get(3);
                    if (status == 0x01) {
                        productIDSetUP.put("updateStatus", 0);
                    }
                    if (status == 0) {
                        productIDSetUP.put("updateStatus", 1);
                    }
                    productIDSetUP.put("control", "firmwareSetting");
                    productIDSetUP.put("action", "productIDSetUP");
                    server.getBroadcastOperations().sendEvent("msgInfo", productIDSetUP);//发送给前端
                }

                //查询FPS信息
                if (pfd.get(1) == 0x10) {
                    JSONObject queryFPS = new JSONObject();
                    int flag = pfd.get(3), FPS = 0;
                    if (flag == 0) {
                        FPS = 50;
                    } else if (flag == 1) {
                        FPS = 100;
                    }
                    queryFPS.put("FPS", FPS);
                    queryFPS.put("control", "queryFPS");
                    server.getBroadcastOperations().sendEvent("msgInfo", queryFPS);//发送给前端
                }

                //handy2数据的转换
                if (pfd.get(1) == 0x01) {
                    PublicVariable.bluetoothFlag = true;
                    FirmNode firmNode = PublicVariable.handyFirmMap.get(deviceId);
                    Integer sensorNumber = firmNode.getSensorNumber();
                    DataProcessingHandy2.actionDataHandy(pfd, sensorNumber);
                }


                //动作数据转换 储存至队列
                if (pfd.get(1) == 0x00) {
                    int sensorNumberMark = pfd.get(39);//传感器序号
                    System.out.println(sensorNumberMark + "---");
                    boolean flag = true;
                    if (sensorNumberMark == 7) {
                        for (FirmSuit firmSuitModel : SocketThreadFirm.firmSuits) {
                            if ("handy(Left)".equals(firmSuitModel.getProductID())) {//handyLeft存在设备列表，左手关节点数据不处理，不存队列使用
                                flag = false;
                            }
                        }
                    }
                    if (sensorNumberMark == 14) {
                        for (FirmSuit firmSuitModel : SocketThreadFirm.firmSuits) {
                            if ("handy(Right)".equals(firmSuitModel.getProductID())) {//handyRight存在设备列表，右手关节点数据不处理，不存队列使用
                                flag = false;
                            }
                        }
                    }
                    if (flag) {
                        ArrayList<Float> floats = new ArrayList<Float>();//放Vein数据
                        //四元素
                        List<Byte> W = null, X = null, Y = null, Z = null, frameDate = null;
                        List<Queue> dataQueuesORI = PublicVariable.dataQueuesORI;
                        List<Queue> dataQueuesACC = PublicVariable.dataQueuesACC;
                        if (X != null || Y != null || Z != null) {
                            float XData = ToDataUtils.byte2float(ToDataUtils.ListToByte(X), 0);
                            float YData = ToDataUtils.byte2float(ToDataUtils.ListToByte(Y), 0);
                            float ZData = ToDataUtils.byte2float(ToDataUtils.ListToByte(Z), 0);
                            float frame = ToDataUtils.bytesToIntsmall(ToDataUtils.ListToByte(frameDate), 0);
                            if (W != null) {
                                float WData = ToDataUtils.byte2float(ToDataUtils.ListToByte(W), 0);
                                if (((WData * WData + XData * XData + YData * YData + ZData * ZData) - 1) <= 0.0001) {
                                    floats.add(0, WData);
                                    floats.add(1, XData);
                                    floats.add(2, YData);
                                    floats.add(3, ZData);
                                    if (sensorNumberMark != 0 && nodeIndex >= 0 && nodeIndex < 17) {
                                        ParseDataFirm.i[sensorNumberMark - 1]++;
                                        double[] ri = {WData, XData, YData, ZData};
                                        dataQueuesORI.get(sensorNumberMark - 1).offer(ri);//存队列
                                    }
                                }
                            } else {
                                floats.add(0, XData);
                                floats.add(1, YData);
                                floats.add(2, ZData);
                                if (sensorNumberMark != 0 && nodeIndex >= 0 && nodeIndex < 17) {
                                    ParseDataFirm.j[sensorNumberMark - 1]++;
                                    double[] ac = {XData, YData, ZData};
                                    dataQueuesACC.get(sensorNumberMark - 1).offer(ac);
                                }
                            }
                        }
                    }
                }



            }

            // socket.shutdownInput();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private static int suitIsExist(String productID){
        for (int i = 0; i < firmSuits.size(); i++) {
            if (productID.equals(firmSuits.get(i).getProductID())){
                return i;
            }
        }
        return -1;
    }

    /**
     * //通过mac判断设备列表中同一套装下是否有该节点设备存在,如果存在则返回该节点在设备列表中的下标，不存在返回0
     * @param firmNodes
     * @param Mac
     * @return
     */
    private static int nodeIsExist(CopyOnWriteArrayList<FirmNode> firmNodes, String Mac){
        for (int i = 0; i < firmNodes.size(); i++) {
            if (firmNodes.get(i)!=null&&firmNodes.get(i).getMAC().equals(Mac)) return i;
        }
        return -1;
    }

    /**
     * //判断节点是否为NULL,true为NULL,false不为NULL
     * @param suit
     * @return
     */
    private static boolean nodesIsEmpty(FirmSuit suit){
        CopyOnWriteArrayList<FirmNode> firmNodes = suit.getFirmNodes();
        for (FirmNode firmNode : firmNodes) {
            if (null!=firmNode) return false;
        }
        return true;
    }


    /**
     * list<byte>转Byte[]
     * @param list
     * @return
     */
    public static int[] ListToByte(List<Integer> list){
        if (list == null || list.size()<0) return null;

        int [] arr = new int[list.size()];
        Iterator<Integer> iterator = list.iterator();
        int i = 0;
        while (iterator.hasNext()){
            arr[i] = iterator.next();
            i++;
        }
        return arr;
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
    public static int bytesToIntsmall(int[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24);
        return value;
    }


    //这个函数将byte转换成float
    public static float byte2float(int[] b, int index) {
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
    public static String bytesToHex(int[] bytes) {
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
