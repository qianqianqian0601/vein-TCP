package com.huantek.vein.Model;

import lombok.Data;

import java.util.HashMap;

@Data
public class ALGData {
    private String controlField;
    private String dataLength;
    private String frameNumber;
    private HashMap<String,Object> dataVolume;
    private String W;
    private String X;
    private String Y;
    private String Z;
}
