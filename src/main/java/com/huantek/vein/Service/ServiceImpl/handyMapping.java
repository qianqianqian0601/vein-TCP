package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.Model.FirmNode;
import com.huantek.vein.Model.FirmSuit;
import com.huantek.vein.socket.SocketThreadFirm;

import java.util.concurrent.CopyOnWriteArrayList;

public class handyMapping {

    public void handyAssort(JSONObject jsonObject) {
        String productID = jsonObject.getString("productID");//要匹配的套装产品ID
        String productIDHandy = jsonObject.getString("productIDHandy");//handy现在的套装ID
        String macHandy = jsonObject.getString("macHandy");//handy的Mac
        CopyOnWriteArrayList<FirmSuit> firmSuits = SocketThreadFirm.firmSuits;
        for (FirmSuit firmSuit : firmSuits) {
            if (firmSuit.getProductID().equals(productIDHandy)){//寻找handy的套装
                CopyOnWriteArrayList<FirmNode> firmNodes = firmSuit.getFirmNodes();//获取handy的套装节点
                for (FirmNode firmNode : firmNodes) {
                    if (firmNode.getMAC().equals(macHandy)){//寻找handy的节点
                        for (FirmSuit suit : firmSuits) {
                            if (suit.getProductID().equals(productID)){//寻找要配对的套装
                                firmNodes.add(firmNode.getSensorNumber()-1,firmNode);//添加handy的节点
                            }
                        }
                        firmNodes.remove(firmNode);//删除handy在原本的套装中
                    }
                }
            }
        }
    }
}
