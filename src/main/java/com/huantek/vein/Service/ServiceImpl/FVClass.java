package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOServer;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class FVClass {

    private ConcurrentHashMap<String,Socket> socketMap;
    public FVClass(ConcurrentHashMap<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    //根据参数获取调用方法
    public void firmwareVersion(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if (action!=null||!"".equals(action)){
            if ("singleUP".equals(action)) singleUP(jsonObject);//单个升级
            if ("allUP".equals(action)) allUP(jsonObject);//整套升级
        }else {
            System.out.println("参数未传入");
        }
    }

    private void singleUP(JSONObject jsonObject) {//单个升级
        String suit = jsonObject.getString("productID");
        String mac = jsonObject.getString("MAC");
        String url = jsonObject.getString("URL");
        String socketName = suit + mac;
        sendFirVersionUPMSG(socketName,url);
    }

    private void allUP(JSONObject jsonObject) {//整套升级
        String suit = jsonObject.getString("productID");
        JSONArray macs = jsonObject.getJSONArray("MAC");
        String url = jsonObject.getString("URL");
        for (Object mac : macs) {
            String socketName = suit + mac;
            sendFirVersionUPMSG(socketName,url);
        }
    }

    //发送固件版本升级Url
    public void sendFirVersionUPMSG(String socketName,String url) {
        OutputStream outputStreamFir;
        Socket socket;
        try {
            String DNS = url.substring(7, 47);//文件DNS
            int length = url.length();
            String path = url.substring(48, length);//文件储存路径
            path = "/"+path;
            byte[] dnsByte = ByteZeroFill(DNS.getBytes(), 60);//dns转Byte[]补零
            byte[] pathByte = ByteZeroFill(path.getBytes(), 64);//dns转Byte[]补零
            byte len = (byte) (dnsByte.length + pathByte.length);
            socket = socketMap.get(socketName);
            if (socket!=null){
                outputStreamFir = socket.getOutputStream();//固件输出流
                byte[] arr = {0x47,0x05,len};//固件升级详情
                ByteArrayOutputStream stream = new ByteArrayOutputStream();//拼接Byte[]
                stream.write(arr,0,arr.length);
                stream.write(dnsByte,0,dnsByte.length);
                stream.write(pathByte,0,pathByte.length);
                stream.write(0x74);
                byte[] VersionUp = stream.toByteArray();
                System.out.println("----");
                outputStreamFir.write(VersionUp);//查询设备详情
                System.out.println("正在进行软件升级----");
                for (int i = 0; i < VersionUp.length; i++){
                    System.out.print(VersionUp[i]+" ");
                }
                System.out.println("----");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 数组补零
     * @param bytes 需要补零的数组
     * @param len  最终长度
     * @return
     */
    public static byte[] ByteZeroFill(byte[] bytes , int len){
        if(bytes.length<len){
            byte[] arr = new byte[len];
            for (int i = 0;i<bytes.length;i++){
                arr[i] = bytes[i];
            }
            return arr;
        }else {
            return bytes;
        }
    }


}
