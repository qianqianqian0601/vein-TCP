package com.huantek.vein.socket;

import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import com.huantek.jni.conversionData.ConversionData;
import com.huantek.jni.conversionData.ConversionDataHandy2;
import com.huantek.jni.conversionData.KeyFrameInterpolation;
import com.huantek.vein.Model.FirmNode;
import com.huantek.vein.Model.FirmSuit;
import com.huantek.vein.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class SocketThreadFirm extends Thread{
    //创建和本线程相关的Socket
    private ConcurrentHashMap<String,Socket> socketMap = null;
    private SocketIOServer server = null;
    private Vector<Byte> bytes = null;
    private int number,count;
    //    public  static JSONObject VeinData = new JSONObject();
    private String socketName;
    public static CopyOnWriteArrayList<FirmSuit> firmSuits;//设备套装列表
    public SocketThreadFirm(SocketIOServer server, ConcurrentHashMap<String, Socket> socketMap, int numBer, int count, CopyOnWriteArrayList<FirmSuit> firmSuits){
        this.socketMap = socketMap;
        this.server = server;
        this.number = numBer;
        this.count = count;
        SocketThreadFirm.firmSuits = firmSuits;
    }

    @SneakyThrows
    @Override
    public void run(){
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        FirmNode firmNode;//初始化设备节点对象
        FirmSuit firmSuit;//初始化设备套装对象
        InputStream inputStream = null;
        OutputStream outputStreamAlg = null;
        OutputStream outputStreamFir = null;
        Socket socket = null;
        Integer sensorNumber = null;//序号区分节点
        String mac = null;//建立一个该方法内使用的公共mac
        String productID = null;//建立一个该方法内使用的公共productID



        try {
            socket = socketMap.get("Firmware"+number);
            socket.setKeepAlive(true);//保持活跃的socket开启
            socket.setSoTimeout(5000);//设置超时时间
            if (socket!=null){
                outputStreamFir = socket.getOutputStream();//固件输出流
                outputStreamFir.write(OrderBase.QUERY_FIRMWARE_INFO);//查询设备详情
                outputStreamFir.write(OrderBase.FPS_QUERY);//查询设备详情
//                if (ConversionData.time==18){
//                    outputStreamFir.write(OrderBase.FPS_50);//设置传感器帧率与后端匹配
//                }else {
//                    outputStreamFir.write(OrderBase.FPS_100);//设置传感器帧率与后端匹配
//                }
                inputStream = socket.getInputStream();//获取输入流读取客户端信息
                int info;
                bytes = new Vector<>();//使用Vector动态存储Byte存储固件数据
                JSONObject nodeData = new JSONObject();//节点数据
                firmNode = new FirmNode();//创建设备节点对象
                int testCount = 0 , nodeIndex = 0;
                while ((info = inputStream.read())!=-1){//循环读，-1表示读完
                    bytes.add((byte) info);//将数据存入Vector

                    List<Byte> pfd = parseFirmwareData();

                    if (pfd!=null){
                        ParseDataFirm parseData = new ParseDataFirm(pfd);//数据解析
                        parseData.returnParseData(nodeData,sensorNumber,nodeIndex);
                        //设备详情数据
                        if (pfd.get(1)==0x04){
                            sensorNumber = nodeData.getInteger("sensorNumber");//传感器序号
                            System.out.println(sensorNumber);
                            mac = nodeData.getString("MAC");
                            Float firmWareVersion = nodeData.getFloat("firmWareVersion");//固件版本
                            productID = nodeData.getInteger("productID").toString();//产品ID
                            Integer pickUpLevel = nodeData.getInteger("pickUpLevel");//传感器校准等级
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
                            socketName = productID+mac;//拼接socket名称，产品ID加MAC
                            //String key = "";
                            Iterator<Map.Entry<String, Socket>> iterator = socketMap.entrySet().iterator();
                            while (iterator.hasNext()){
                                Map.Entry<String, Socket> next = iterator.next();
                                Socket value = next.getValue();
                                if (Objects.equals(value,socket)){//对比map的value,获取key
                                    iterator.remove();
                                }
                            }
                            socketMap.put(socketName, socket);

                            firmNode.setMAC(mac);//存入对象属性
                            firmNode.setFirmVersion(firmWareVersion);
                            firmNode.setSensorNumber(sensorNumber);
                            firmNode.setPickUpLevel(pickUpLevel);

                            firmSuit = new FirmSuit();
                            firmSuit.setProductID(productID);

                            int subScript = suitIsExist(productID);//判断设备列表中是否存在该产品ID的设备，true存在，false不存在
                            if (subScript==-1){//当列表当中不存在该产品ID的设备时
                                //if (firmSuits.isEmpty()){
                                //  FirmSuit initSuit = new FirmSuit();
                                // initSuit.setProductID("");
                                //initSuit.setFirmNodes(null);
                                //firmSuits.add(initSuit);//初始化设备列表，使下次可以正常满足连接条件
                                //}
                                if (!firmSuits.isEmpty()&&firmSuits.get(0).getProductID().equals("")){//判断设备列表是不是初始化的，是初始化的覆盖在0下标上
                                    firmSuits.set(0,firmSuit);
                                }else {//不是初始化的在列表数组后追加
                                    firmSuits.add(firmSuit);
                                }
                            }else {
                                // firmSuits.set(subScript,firmSuit);
                                FirmSuit Suit = firmSuits.get(subScript);
                                CopyOnWriteArrayList<FirmNode> firmNodes = Suit.getFirmNodes();
                                if (sensorNumber>0){
                                    firmNodes.set(sensorNumber-1,firmNode);
                                }else if (sensorNumber==0){
                                    int i = nodeIsExist(firmNodes, mac);
                                    if (i==-1){
                                        Suit.getFirmNodes().add(firmNode);
                                    }
                                }
                            }

                            if (PublicVariable.newConnectTime != 0) {
                                long timeMillis = System.currentTimeMillis();//当前时间
                                long l = timeMillis - PublicVariable.newConnectTime;//与上一连接相差时间
                                if (l > 500 && !socketMap.isEmpty()) {//连接超过500ms并且地址不为空的发送停止命令
                                    long num = socketMap.values().stream().count();
                                    if (num==socketMap.size()) {
                                        for (Socket socketModel : socketMap.values()) {
                                            //发送停止指令
                                            socketModel.getOutputStream().write(OrderBase.STOP_MOTION_CAPTURE);
                                        }
                                        System.out.println("所有节点停止发送数据---");
                                        sleep(10);
                                        PublicVariable.connectFlag = false;
                                    }
                                }
                            }
                            PublicVariable.newConnectTime = System.currentTimeMillis();//记录新的连接时间

                            nodeData = new JSONObject();//初始化nodeData
                        }

                        if (sensorNumber==null){//没有查询到节点 继续查询
                            outputStreamFir.write(OrderBase.QUERY_FIRMWARE_INFO);//查询设备详情指令
                            continue;
                        }

                        //电量信息查询
                        if (pfd.get(1)==0x03){
                            Integer cellPrice = nodeData.getInteger("cellPrice");
                            firmNode.setCellPrice(cellPrice);//存入对象属性
                            nodeData = new JSONObject();//初始化nodeData
                        }

                        //传感器校准等级
                        if (pfd.get(1)==0x12){
                            server.getBroadcastOperations().sendEvent("msgInfo",nodeData);
                            if (pfd.get(3)==0x02){
                                Integer pickUpLevel = nodeData.getInteger("pickUpLevel");
                                firmNode.setPickUpLevel(pickUpLevel);
                            }
                            nodeData = new JSONObject();//初始化nodeData
                        }


                        // handy2校准 1
                        if (pfd.get(1)==0x13 && SocketServer.softwareFlag.equals("Handy2")){
                            PublicVariable.handyFlagOne = false;
                            int sensorNumberHandy = pfd.get(3);
                            int type = pfd.get(4);
                            if (type==2){//校准
                                if (sensorNumberHandy == 18) ConversionDataHandy2.handy2Pose = 0;
                                if (sensorNumberHandy == 19) ConversionDataHandy2.handy2Pose = 1;
                            }
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


                        //信号强度查询
                        if (pfd.get(1)==0x0A){
                            Integer signal = nodeData.getInteger("signal");
                            firmNode.setSignal(signal);//存入对象属性
                            nodeData = new JSONObject();//初始化nodeData

                            for (FirmSuit suit : firmSuits) {//遍历设备列表获取单独的套装设备
                                if (suit.getProductID().equals(productID)){//判断产品ID获取同一套设备节点信息
                                    CopyOnWriteArrayList<FirmNode> firmNodes = suit.getFirmNodes();
                                    //通过mac判断设备列表中同一套装下是否有该节点设备存在
                                    //如果存在则返回该节点在设备列表中的下标，不存在返回-1
                                    int num = nodeIsExist(firmNodes,mac);
                                    nodeIndex = num;
                                    if (num>-1&&num<19){//大于-1的话列表中存在该节点信息,小于17证明该节点有对应部位

                                        if(sensorNumber>0){//节点信息大于0的时候
                                            if (firmNodes.get(sensorNumber-1)==null){//该下标目前为空就放在改下标处
                                                firmNodes.set(num,null);
                                                firmNodes.set(sensorNumber-1,firmNode);
                                            }
                                        }else if (sensorNumber==0){//等于0为临时节点
                                            firmNodes.set(num,null);
                                            firmNodes.add(firmNode);//临时节点追加在后面
                                        }
                                    }else if(num>=19){//大于等于17证明该节点之前是临时节点
                                        if (sensorNumber>0){//节点改变成大于0的数,将之前临时下标的节点信息删除，并将该节点信息放在对应下标上
                                            if (firmNodes.get(sensorNumber-1)==null){
                                                firmNodes.remove(num);
                                                firmNodes.set(sensorNumber-1,firmNode);
                                            }else if(firmNodes.get(sensorNumber-1)!=null && !firmNodes.get(sensorNumber-1).equals(mac)){
                                                continue;
                                            }
                                        }//如果节点还是为0，不做任何处理
                                    }else if (num==-1){//等于-1列表不存在该节点信息
                                        if (sensorNumber>0){//节点号不为0,则放在对应的下标上
                                            if (firmNodes.get(sensorNumber-1)==null){
                                                firmNodes.set(sensorNumber-1,firmNode);
                                            }else if(firmNodes.get(sensorNumber-1)!=null && !firmNodes.get(sensorNumber-1).getMAC().equals(mac)){
                                                firmNodes.add(firmNode);
                                            }
                                        }else if(sensorNumber==0){//节点号为0,则向后追加临时节点
                                            firmNodes.add(firmNode);
                                        }
                                    }
                                }
                            }
                            boolean isEmpty = nodesIsEmpty(firmSuits.get(0));
                            if (isEmpty){
                                firmSuits.remove(0);
                            }
                            if (firmSuits==null){
                                firmSuits = IntegratedData.initFirmSuitList();
                            }
                        }

                        //固件升级返回信息
                        if (pfd.get(1)==0x05){
                                String updateStatus = nodeData.getString("updateStatus");
                                System.out.println("updateStatus:"+updateStatus);
                                server.getBroadcastOperations().sendEvent("msgInfo" , nodeData);//发送给前端
                                nodeData = new JSONObject();//初始化nodeData
                        }


                        //固件升级返回信息
                        if (pfd.get(1)==0x07){
                            String updateStatus = nodeData.getString("updateStatus");
                            System.out.println("updateStatus:"+updateStatus);
                            server.getBroadcastOperations().sendEvent("msgInfo" , nodeData);//发送给前端
                            nodeData = new JSONObject();//初始化nodeData
                        }

                        //节点配置返回信息
                        if (pfd.get(1)==0x08){
                            String updateStatus = nodeData.getString("updateStatus");
                            System.out.println("updateStatus:"+updateStatus);
                            server.getBroadcastOperations().sendEvent("msgInfo" , nodeData);//发送给前端
                            nodeData = new JSONObject();//初始化nodeData
                        }

                        //产品ID配置返回信息
                        if (pfd.get(1)==0x09){
                            String updateStatus = nodeData.getString("updateStatus");
                            System.out.println("updateStatus:"+updateStatus);
                            server.getBroadcastOperations().sendEvent("msgInfo" , nodeData);//发送给前端
                            nodeData = new JSONObject();//初始化nodeData
                        }

                        //查询FPS信息
                        if (pfd.get(1)==0x10){
                            Integer FPS = nodeData.getInteger("FPS");
                            System.out.println("FPS:"+FPS);
                            server.getBroadcastOperations().sendEvent("msgInfo" , nodeData);//发送给前端
                            nodeData = new JSONObject();//初始化nodeData
                        }

                        //动作数据转换 储存至队列
                        if (pfd.get(1)==0x00){
                            int sensorNumberMark = pfd.get(39);//传感器序号
                            boolean flag = true;
                            if (sensorNumberMark==7){
                                for (FirmSuit firmSuitModel : SocketThreadFirm.firmSuits) {
                                    if (firmSuitModel.getProductID().equals("handy(Left)")){//handyLeft存在设备列表，左手关节点数据不处理，不存队列使用
                                        flag = false;
                                    }
                                }
                            }
                            if (sensorNumberMark==14){
                                for (FirmSuit firmSuitModel : SocketThreadFirm.firmSuits) {
                                    if (firmSuitModel.getProductID().equals("handy(Right)")){//handyRight存在设备列表，右手关节点数据不处理，不存队列使用
                                        flag = false;
                                    }
                                }
                            }
                            if (flag){
                                //四元素
                                List<Byte>  W, X , Y , Z  , XX , YY  , ZZ  ,  frameDate ;
                                frameDate = pfd.subList(3, 7);
                                W = pfd.subList(7, 11);
                                X = pfd.subList(11, 15);
                                Y = pfd.subList(15, 19);
                                Z = pfd.subList(19, 23);
                                XX = pfd.subList(23, 27);
                                YY = pfd.subList(27, 31);
                                ZZ = pfd.subList(31, 35);
                                float WData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(W), 0);
                                float XData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(X), 0);
                                float YData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(Y), 0);
                                float ZData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(Z), 0);
                                float XXData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(XX), 0);
                                float YYData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(YY), 0);
                                float ZZData = TransformUtil.byte2floatParamByte(TransformUtil.ListByteToByte(ZZ), 0);
                                int frame = TransformUtil.bytesToIntsmall(TransformUtil.ListByteToByte(frameDate), 0);
                                if (((WData * WData + XData * XData + YData * YData + ZData * ZData) - 1) <= 0.0001) {
                                    if (sensorNumber != null && !sensorNumber.equals(0) && nodeIndex >= 0 && nodeIndex < 17) {
                                        NonBlockingHashMap<Integer, double[]> map = PublicVariable.dataHashMapsORI.get(sensorNumber - 1);
                                        double[] oriAndAcc = {WData, XData, YData, ZData,XXData,YYData,ZZData};
                                        if (map.containsKey(frame)){
                                            map.replace(frame,oriAndAcc);
                                        }else {
                                            map.put(frame,oriAndAcc);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                socket.shutdownInput();
            }

        } catch (SocketException e) {
            System.out.println("socketThreadFirm");
            System.out.println("设备：：：："+sensorNumber);
            e.printStackTrace();
        }catch (SocketTimeoutException e) {
            System.out.println("socketThreadFirm");
            System.out.println("设备：：：："+sensorNumber);
            e.printStackTrace();
        } finally {
            if (inputStream!=null) inputStream.close();
            if (outputStreamAlg!=null) outputStreamAlg.close();
            if (outputStreamFir!=null) outputStreamFir.close();
            if (socket!=null) {
                if (firmSuits!=null){//设备列表不为NULL时
                    Iterator<FirmSuit> iterator = firmSuits.iterator();//获取迭代器，因为需要删除元素，for循环会抛出ConcurrentModificationException异常
                    while (iterator.hasNext()){//判断是否还有下一个元素
                        FirmSuit suit = iterator.next();//获取下一个元素，元素是Suit对象
                        if (suit.getProductID().equals(productID)){//获取同一套设备对象
                            CopyOnWriteArrayList<FirmNode> firmNodes = suit.getFirmNodes();//获取该套设备的节点信息
                            //if (sensorNumber!=null&&sensorNumber>0){//当节点大于零的时候
                            //  firmNodes.set(sensorNumber-1,null);//将对应节点上的信息，替换成null值
                            //}else {//当节点为零的时候
                            for (int i=0;i<firmNodes.size();i++) {//遍历节点信息
                                if (firmNodes.get(i)!=null&&firmNodes.get(i).getMAC().equals(mac)){//找到对应Mac的节点
                                    firmNodes.set(i,null);//将对应节点信息替换成null值
                                }
                            }
                            //}
                            boolean nodesIsEmpty = nodesIsEmpty(suit);//判断节点是否为NULL,true为NULL,false不为NULL
                            if (nodesIsEmpty){//当节点信息为空的时候
                                firmSuits.remove(suit);//删除设备列表中相对的套装信息
                            }
                            if (firmSuits.isEmpty()){//当设备列表为null的时候
                                FirmSuit initSuit = new FirmSuit();
                                initSuit.setProductID("");
                                initSuit.setFirmNodes(null);
                                firmSuits.add(initSuit);//初始化设备列表，使下次可以正常满足连接条件
                            }
                        }
                    }

                }
                Integer finalSensorNumber = sensorNumber;
                PublicVariable.connectingList.removeIf(a-> a.equals(finalSensorNumber));
                count--;
                if (socketMap.containsKey(socketName)){
                    socketMap.remove(socketName,socket);
                }
            }
        }
    }



    /**
     * 读取固件数据报文
     * @return
     */
    public List<Byte> parseFirmwareData() {
        for (int i=0;i<bytes.size();i++){//循环Vector
            if (bytes.get(i)==0x47){//首先找到帧头
                if (bytes.size()<i+3){//避免在获取数据长度时数组越界
                    return null;
                }
                int len = (bytes.get(i+2)&0xff)+3;//获取报文长度
                if (bytes.size()-1-i >= len){//长度足够
                    if (bytes.get(i+len)==0x74){//找帧尾
                        List<Byte> sub;
                        Vector<Byte> clone = (Vector<Byte>) bytes.clone();
                        sub = clone.subList(i,i+len+1);//将获取的完整报文放在新的容器中
                        bytes.subList(0,i+len+1).clear();//将已经获取到的报文删除，继续获取下一个报文
                        return sub;
                    }else {//继续循环找帧尾
//                        Vector<Byte> errorData = (Vector<Byte>) bytes.clone();
//                        List<Byte> errorDataList = errorData.subList(i, i + len + 1);
//                        LostFrame.errorDataLogFirm(errorDataList);
                        bytes.subList(0,i+len+1).clear();
                        continue;
                    }
                }else {//长度不够继续向Vector存数据
                    return null;
                }
            }else {//继续循环找帧头
                continue;
            }
        }
        return null;
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


    /**
     * 数组补零
     * @param bytes 需要补零的数组
     * @param len  最终长度
     * @return
     */
    public static byte[] ByteZeroFill(byte[] bytes , int len){
        if(bytes.length<64){
            byte[] arr = new byte[len];
            for (int i = 0;i<bytes.length;i++){
                arr[i] = bytes[i];
            }
            return arr;
        }else {
            return bytes;
        }
    }

    /**
     * //判断设备列表中是否存在该产品ID的设备，-1不存在，存在返回对象下标
     * @param productID
     * @return
     */
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

}
