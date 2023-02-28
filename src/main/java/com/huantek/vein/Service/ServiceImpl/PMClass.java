package com.huantek.vein.Service.ServiceImpl;

import com.alibaba.fastjson.JSONObject;
import com.huantek.vein.Mapper.PMDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 类：项目管理
 * 创建人：yx.
 * 程序人员写程序，又将程序换酒钱；
 * 酒醒只在屏前坐，酒醉还来屏下眠；
 */
@Component
public class PMClass  {
    @Autowired
    private PMDao pmDao;

    public static PMClass pmClass;

    @PostConstruct
    public void init(){
        pmClass = this;
        pmClass.pmDao = this.pmDao;
    }

    public  String demoManagement(JSONObject jsonObject){
        String operator = jsonObject.getString("operator");
        if ("创建项目".equals(operator)) return createDemo(jsonObject);
        return "无该指令操作";
    }

    //创建项目
    public  String createDemo(JSONObject jsonObject){
        JSONObject json = new JSONObject();
        String demoName = jsonObject.getString("demoName");
        String demoPath = jsonObject.getString("demoPath");
        String isByStl = jsonObject.getString("isByStl");
        try {
            if ("0".equals(isByStl)) pmClass.pmDao.createDemo(demoName,demoPath);//创建项目不绑定默认骨骼数据
            else if ("1".equals(isByStl)) pmClass.pmDao.createDemoByDefaultBone(demoName,demoPath,1);//创建项目绑定默认骨骼数据
            else {
                json.put("dataObject",null);
                json.put("success",false);
                json.put("message","输入正确绑定骨骼指令");
                json.put("errorCode",0);
                return json.toJSONString();
            }
            json.put("dataObject",null);
            json.put("success",true);
            json.put("message","项目创建成功");
            json.put("errorCode",0);
            return json.toJSONString();
        }catch (Exception e){
            e.printStackTrace();
            json.put("dataObject",null);
            json.put("success",false);
            json.put("message","项目创建失败");
            json.put("errorCode",0);
            return json.toJSONString();
        }
    }

}
