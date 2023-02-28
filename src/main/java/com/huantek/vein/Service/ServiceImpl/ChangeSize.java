package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huantek.jni.conversionData.declareFunc;

/**
 * 修改模型大小
 */
public class ChangeSize {

    static declareFunc tmp = new declareFunc();
    public void changeModelSize(JSONObject jsonObject) {
        JSONArray sizeArray = jsonObject.getJSONArray("size");
        double[] doubles = new double[sizeArray.size()];
        for (int i = 0; i < sizeArray.size(); i++) {//jsonArray转double[]
            doubles[i] = (double) sizeArray.get(i);
        }
        //tmp.setSize(doubles);
    }
}
