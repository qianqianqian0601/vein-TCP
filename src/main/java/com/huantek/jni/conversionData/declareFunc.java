package com.huantek.jni.conversionData;

public class declareFunc {

    //保存c++类的地址
    long nativePerson;

    static {
        System.loadLibrary("VeinModel");//将dll放在jre的bin目录下 直接在运行环境中加载
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //构造函数
    public declareFunc(){
        nativePerson = createVein();
    }

    public void calibration(
            int pose,
            double[] devicePosture,
            double[] dstModelStatus
    ){
        calibration(nativePerson, pose, devicePosture, dstModelStatus);
    }

    public void update(//111
                       double[] devicePosture,
                       double[] devicePosition,
                       double[] dstModelStatus,
                       double[] dstModelStatus2
    ){
        update(nativePerson, devicePosture, devicePosition, dstModelStatus , dstModelStatus2);
    }

    public void setSize(double[] stretchLen){
        setSize(nativePerson, stretchLen);
    }

    public void resetPos(){
        resetPos(nativePerson);
    }

    public void pressKey(int key){
        pressKey(nativePerson , key);
    }



    /**本地方法：创建c++对象并返回地址*/
    private native long createVein();

    private native void calibration(
            long addr,
            int pose,
            double[] devicePosture,
            double[] dstModelStatus
    );

    private native void update(
            long addr,
            double[] devicePosture,
            double[] devicePosition,
            double[] dstModelStatus,
            double[] dstModelStatus2
    );

    private native void setSize(
            long addr,
            double[] stretchLen
    );

    private native void resetPos(
            long addr
    );

    private native void pressKey(
            long addr,
            int key
    );

}
