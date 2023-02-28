package com.huantek.jni.conversionData;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class QuatSlerp {

    public interface QLibray extends Library {
            QLibray INSTANCE =
                    (QLibray) Native.loadLibrary(
                            "QuatSlerp",
                            QLibray.class);

            void slerp(double[] q1, double[] q2, double gamma, double[] output);

    }
}
