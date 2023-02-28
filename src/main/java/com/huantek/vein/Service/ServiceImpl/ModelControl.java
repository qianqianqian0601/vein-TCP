package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huantek.jni.conversionData.ConversionData;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelControl {

    public void modelCommand(JSONObject jsonObject) {
        String action = jsonObject.getString("action");
        if ("setBoneSize".equals(action)){
            setBoneSize(jsonObject);
        }
        if ("backToTheStart".equals(action)){
            backToTheStart();
        }
    }

    /**
     * 回到原点
     */
    private void backToTheStart() {
        try {
            ConversionData.tmp.resetPos();
            System.out.println("回到原点成功");
        }catch (Exception e){
            System.out.println("回到原点失败");
        }
    }

    /**
     * 改变模型大小
     */
    private void setBoneSize(JSONObject jsonObject) {
        try {
            if (jsonObject.containsKey("bonesList")){
                JSONArray bonesList = jsonObject.getJSONArray("bonesList");
                List<Double[]> list = bonesList.toJavaList(Double[].class);
                int length = 0 , index = 0;
                for (Double[] bones : list) {
                    length+=bones.length;
                }
                double[] doubles = new double[length];
                for (Double[] bones : list) {
                    for (Double bone : bones) {
                        doubles[index] = bone;
                        index++;
                    }
                }
                ConversionData.tmp.setSize(doubles);
            }
            System.out.println("调用完成");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
