package com.huantek.vein.util;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class ParseDataAlg {

    //接收本线程相关数据
    public List<Byte> byteData = null;

    public ParseDataAlg(List<Byte> pfd) {
        this.byteData = pfd;
    }

    public JSONObject returnParseData(){
        try {
            Byte controlFieldData = byteData.get(1);
            JSONObject veinData = null;
            if (controlFieldData==0x04){
                List<Byte> data = byteData.subList(6, byteData.size()-1);
                String s = new String(ListToByte(data), "UTF-8");
                veinData = JSONObject.parseObject(s);
            }
            return veinData;
        }catch (Exception e){
            e.printStackTrace();
            return null;
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
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序-小端序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToIntSmall(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序-大端序。和intToBytes2（）配套使用
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
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
     * 将byte数组转化成String,为了支持中文，转化时用GBK编码方式
     */
    public static String ByteArraytoString(byte[] valArr,int maxLen) {
        String result=null;
        int index = 0;
        while(index < valArr.length && index < maxLen) {
            if(valArr[index] == 0) {
                break;
            }
            index++;
        }
        byte[] temp = new byte[index];
        System.arraycopy(valArr, 0, temp, 0, index);
        try {
            result= new String(temp,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
