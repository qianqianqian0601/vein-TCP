package com.huantek.vein.Model;

import lombok.Data;


/**
 * 设备节点信息
 */
@Data
public class FirmNode {
    private String productID;//节点MAC
    private String MAC;//节点MAC
    private float firmVersion;//节点固件版本
    private Integer sensorNumber;//节点号
    private Integer signal;//信号
    private Integer cellPrice = 0;//电量
    private Integer pickUpLevel;//校准等级
}
