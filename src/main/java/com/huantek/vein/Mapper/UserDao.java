package com.huantek.vein.Mapper;

import com.huantek.vein.Model.UserBean;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserDao {

    @Insert("insert into hello(id,title,text) values(#{userBean.id},#{userBean.title},#{userBean.text})")
    void insertUser(@Param("userBean") UserBean userBean);

    @Select("select * from hello")
    List<UserBean> queryUser();

    @Update("update hello set text = #{text} where id = 1")
    void updateUser(String text);
}
