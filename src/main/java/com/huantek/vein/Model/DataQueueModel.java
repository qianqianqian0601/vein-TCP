package com.huantek.vein.Model;

import lombok.Data;

import java.util.concurrent.BlockingQueue;

@Data
public class DataQueueModel {
    public BlockingQueue<String> queue;
}
