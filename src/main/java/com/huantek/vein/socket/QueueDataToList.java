package com.huantek.vein.socket;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class QueueDataToList {
    /**
     * 去除重复的连接节点
     *
     * @param connectingList
     * @return
     */
    public List<Integer> removeDuplicate(List<Integer> connectingList) {
        return connectingList.stream().distinct().filter(a->a!=0).collect(Collectors.toList());
    }
}
