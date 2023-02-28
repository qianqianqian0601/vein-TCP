package com.huantek.jni.conversionData;

import com.huantek.vein.Service.ServiceImpl.BVH2FBX;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class declareFuncHandy {


    /**
     *检模块函数映射
     */
    public interface CLibrary extends Library {
        CLibrary INSTANCE =
                (CLibrary) Native.loadLibrary(
                      "HandyModel"
                      ,CLibrary.class);
        void createHandy2();
        void update(double[] posture , double[] outputPre , double[] outputCur);
        void calibration(int handId , double[] devicePosture , double[] output);
    }

//    //保存c++类的地址
//    long nativePerson;
//
//    static {
//        System.loadLibrary("VeinModel");//将dll放在jre的bin目录下 直接在运行环境中加载
//        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
//    }
//
    //构造函数
    public declareFuncHandy(){
        CLibrary.INSTANCE.createHandy2();
    }



    public void calibration(
            int handId,
            double[] devicePosture,
            double[] output
    ){
        CLibrary.INSTANCE.calibration( handId , devicePosture, output);
    }

    public void update(
            double[] posture,
            double[] outputPre,
            double[] outputCur
    ){
        CLibrary.INSTANCE.update(posture, outputPre, outputCur);
    }
//
//    /**本地方法：创建c++对象并返回地址*/
//    private native long createHandy();
//
//    private native void calibration(
//            long addr,
//            int handId,//左手为0，右手为1
//            double[] devicePosture,//14个传感器的四元数
//            double[] output// 返回值，32个节点的四元数
//    );
//
//    private native void update(
//            long addr,
//            double[] posture,//14个传感器的四元数
//            double[] outputPre,//返回值，32个传感器的四元数，中间插值
//            double[] outputCur//返回值，32个传感器的四元数，当前值
//    );

}
