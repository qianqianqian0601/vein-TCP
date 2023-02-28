package com.huantek.vein.util;

public class DataConversionUtils {


    //小端序int转byte[]
    public static byte[] intToByteArraySmall(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0xff);
        b[1] = (byte) (i >> 8 & 0xff);
        b[2] = (byte) (i >> 16 & 0xff);
        b[3] = (byte) (i >> 24 & 0xff);
        return b;
    }

    //大端序int转byte[]
    public static byte[] intToByteArrayBig(int i) {
        byte[] b = new byte[4];
        b[3] = (byte) (i & 0xff);
        b[2] = (byte) (i >> 8 & 0xff);
        b[1] = (byte) (i >> 16 & 0xff);
        b[0] = (byte) (i >> 24 & 0xff);
        return b;
    }

}
