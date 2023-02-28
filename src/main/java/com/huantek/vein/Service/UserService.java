package com.huantek.vein.Service;


import com.huantek.vein.Model.UserBean;

import java.util.List;

public interface UserService {

    void insertUser(UserBean userBean);

    List<UserBean> queryUser();

    void updateUser(String text);
}
