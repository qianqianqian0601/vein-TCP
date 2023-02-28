package com.huantek.vein.util;

import java.util.Iterator;
import java.util.List;

public class ToDataUtils {
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
