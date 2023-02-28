package com.huantek.vein.util;

import static java.lang.Math.*;
import static java.lang.Math.atan2;

/**
 * 四元素转欧拉角公式
 * 返回数组中的数据排序，按照转角顺序排列
 * 例：
 * XYZ 返回->{alpha(X),beta(Y),gamma(Z)}
 * YZX 返回->{alpha(Y),beta(Z),gamma(X)}
 */
public class QuaternionToEulerAngle {


    public static double[] toEulerAngle(String order, double w, double x, double y, double z) {
        switch (order){
            case "XYZ":
                return X_Y_Z(w,x,y,z);
            case "XZY":
                return X_Z_Y(w,x,y,z);
            case "YZX":
                return Y_Z_X(w,x,y,z);
            case "YXZ":
                return Y_X_Z(w,x,y,z);
            case "ZXY":
                return Z_X_Y(w,x,y,z);
            case "ZYX":
                return Z_Y_X(w,x,y,z);
            case "XYX":
                return X_Y_X(w,x,y,z);
            case "XZX":
                return X_Z_X(w,x,y,z);
            case "YXY":
                return Y_X_Y(w,x,y,z);
            case "YZY":
                return Y_Z_Y(w,x,y,z);
            case "ZXZ":
                return Z_X_Z(w,x,y,z);
            case "ZYZ":
                return Z_Y_Z(w,x,y,z);
            default:
                return X_Y_Z(w,x,y,z);
        }
    }

    /**
     * 1
     * @param w
     * @param x
     * @param y
     * @param z
     * @return XYZ转角
     */
    private static double[] X_Y_Z(double w, double x, double y, double z) {
        double alpha, beta, gamma;
        if (0.5- Math.abs(w * z + x * y) < 0.001){
            double s = Math.signum(w * z + x * y);
            alpha = 2 * s * atan2(z,w);
            beta = s * (PI / 2);
            gamma = 0.00;
        }else {
            alpha = atan2(2 * (w * x - y * z),1 - 2 * (x * x + y * y)) * 180/PI;
            beta = asin(2 * (w * y + x * z)) * 180/PI;
            gamma = atan2(2 * (w * z - x * y),1 - 2 * (y * y + z * z)) * 180/PI;
        }
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 2
     * @param w
     * @param x
     * @param y
     * @param z
     * @return XZY转角
     */
    private static double[] X_Z_Y(double w , double x , double y , double z) {
        double alpha = atan2(2 * (w * x + y * z),1 - 2 * (x * x + z * z)) * 180/PI;
        double beta = asin(2 * (w * z - x * y)) * 180/PI;
        double gamma = atan2(2 * (w * y + x * z),1 - 2 * (y * y + z * z)) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 3
     * @param w
     * @param x
     * @param y
     * @param z
     * @return YXZ转角
     */
    private static double[] Y_X_Z(double w , double x , double y , double z) {
        double alpha = atan2(2 * (w * y + x * z),1 - 2 * (x * x + y * y)) * 180/PI;
        double beta = asin(2 * (w * x - y * z)) * 180/PI;
        double gamma = atan2(2 * (w * z + x * y),1 - 2 * (x * x + z * z)) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 4
     * @param w
     * @param x
     * @param y
     * @param z
     * @return YZX转角
     */
    private static double[] Y_Z_X(double w , double x , double y , double z) {
        double alpha = atan2(2 * (w * y - x * z),1 - 2 * (y * y + z * z)) * 180/PI;
        double beta = asin(2 * (w * z + x * y)) * 180/PI;
        double gamma = atan2(2 * (w * x - y * z),1 - 2 * (x * x + z * z)) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 5
     * @param w
     * @param x
     * @param y
     * @param z
     * @return ZYX转角
     */
    private static double[] Z_Y_X(double w , double x , double y , double z) {
        double alpha = atan2(2 * (w * z + x * y),1 - 2 * (y * y + z * z)) * 180/PI;
        double beta = asin(2 * (w * y - x * z)) * 180/PI;
        double gamma = atan2(2 * (w * x + y * z),1 - 2 * (x * x + y * y)) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 6
     * @param w
     * @param x
     * @param y
     * @param z
     * @return ZXY转角
     */
    private static double[] Z_X_Y(double w , double x , double y , double z) {
        double alpha = atan2(2 * (w * z - x * y),1 - 2 * (x * x + z * z)) * 180/PI;
        double beta = asin(2 * (w * x + y * z)) * 180/PI;
        double gamma = atan2(2 * (w * y - x * z),1 - 2 * (x * x + y * y)) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 7
     * @param w
     * @param x
     * @param y
     * @param z
     * @return XZX转角
     */
    private static double[] X_Z_X(double w , double x , double y , double z) {
        double alpha = atan2(x * z - w * y , x * y + w * z) * 180/PI;
        double beta = acos(1 - 2 * (y * y + z * z)) * 180/PI;
        double gamma = atan2(w * y + x * z , x * y - w * z) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 8
     * @param w
     * @param x
     * @param y
     * @param z
     * @return XYX转角
     */
    private static double[] X_Y_X(double w , double x , double y , double z) {
        double alpha = atan2(w * z + x * y , w * y - x * z) * 180/PI;
        double beta = acos(1 - 2 * (y * y + z * z)) * 180/PI;
        double gamma = atan2(x * y - w * z , w * y + x * z) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 9
     * @param w
     * @param x
     * @param y
     * @param z
     * @return YXY转角
     */
    private static double[] Y_X_Y(double w , double x , double y , double z) {
        double alpha = atan2(x * y - w * z , w * x + y * z) * 180/PI;
        double beta = acos(1 - 2 * (x * x + z * z)) * 180/PI;
        double gamma = atan2(w * z + x * y , w * x - y * z) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 10
     * @param w
     * @param x
     * @param y
     * @param z
     * @return YZY转角
     */
    private static double[] Y_Z_Y(double w , double x , double y , double z) {
        double alpha = atan2(w * x + y * z , w * z - x * y) * 180/PI;
        double beta = acos(1 - 2 * (x * x + z * z)) * 180/PI;
        double gamma = atan2(y * z - w * x , w * z + x * y) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 11
     * @param w
     * @param x
     * @param y
     * @param z
     * @return ZYZ转角
     */
    private static double[] Z_Y_Z(double w , double x , double y , double z) {
        double alpha = atan2(y * z - w * x , w * y + x * z) * 180/PI;
        double beta = acos(1 - 2 * (x * x + y * y)) * 180/PI;
        double gamma = atan2(w * x + y * z , w * y - x * z) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }


    /**
     * 12
     * @param w
     * @param x
     * @param y
     * @param z
     * @return ZXZ转角
     */
    private static double[] Z_X_Z(double w , double x , double y , double z) {
        double alpha = atan2(w * y + x * z , w * x - y * z) * 180/PI;
        double beta = acos(1 - 2 * (x * x + y * y)) * 180/PI;
        double gamma = atan2(x * z - w * y , w * x + y * z) * 180/PI;
        double[] EA = {alpha , beta , gamma};
        return EA;
    }
}
