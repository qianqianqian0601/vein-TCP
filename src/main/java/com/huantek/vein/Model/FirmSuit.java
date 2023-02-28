package com.huantek.vein.Model;

import com.huantek.vein.util.IntegratedData;
import lombok.Data;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 设备套装信息
 */
@Data
public class FirmSuit {
    private String productID;//产品ID
    private CopyOnWriteArrayList<FirmNode> firmNodes = IntegratedData.initFirmNodeList();//节点详情
}
