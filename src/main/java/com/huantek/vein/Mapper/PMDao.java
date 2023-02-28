package com.huantek.vein.Mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PMDao {

    //创建项目不绑定默认骨骼数据
    @Insert("insert into demoTable(demoName,demoPath) values(#{demoName} , #{demoPath})")
    void createDemo(@Param("demoName") String demoName, @Param("demoPath") String demoPath);

    //创建项目绑定默认骨骼数据
    @Insert("insert into demoTable(demoName,demoPath,boneData) values(#{demoName} , #{demoPath} , #{boneData}) ")
    void createDemoByDefaultBone(String demoName, String demoPath, int boneData);
}
