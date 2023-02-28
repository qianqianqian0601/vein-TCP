package com.huantek.vein.util;


import com.huantek.vein.Model.FirmNode;
import com.huantek.vein.Model.FirmSuit;

import java.util.concurrent.CopyOnWriteArrayList;

public class IntegratedData {


    //初始化节点列表，使列表是一个动态数组，并且初始有17个null
    public static CopyOnWriteArrayList<FirmNode> initFirmNodeList(){
        CopyOnWriteArrayList<FirmNode> firmNodes = new CopyOnWriteArrayList<>();
        for (int i=0;i<19;i++){
            firmNodes.add(i, null);
        }
        return firmNodes;
    }

    //初始化设备套装列表，以满足连接设备所需要进行的条件处理
    public static CopyOnWriteArrayList<FirmSuit> initFirmSuitList(){
        CopyOnWriteArrayList<FirmSuit> firmSuits = new CopyOnWriteArrayList<>();
        FirmSuit firmSuit = new FirmSuit();
        firmSuit.setProductID("");
        firmSuit.setFirmNodes(null);
        firmSuits.add(firmSuit);
        return firmSuits;
    }

}
