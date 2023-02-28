package com.huantek.vein.Service.ServiceImpl;

import com.huantek.vein.util.PublicVariable;

import java.util.List;
import java.util.Queue;

public class DataQueue {

    private List<Queue> queuesACC = PublicVariable.dataQueuesACC;
    public void dataQueueCount() {
        int maxLength = 0;
        for (int i = 0; i < queuesACC.size(); i++) {
            if (queuesACC.get(i).size()>maxLength){
                maxLength = queuesACC.get(i).size();
            }
        }
        System.out.println("现缓存最大帧数：" + maxLength);
        int minLength = maxLength;
        for (int i = 0; i < queuesACC.size(); i++) {
            if (queuesACC.get(i).size()<minLength){
                minLength = queuesACC.get(i).size();
            }
        }
        System.out.println("现缓存最小帧数：" + minLength);
    }
}
