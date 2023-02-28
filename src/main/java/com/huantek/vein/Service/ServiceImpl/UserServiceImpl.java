package com.huantek.vein.Service.ServiceImpl;

import com.huantek.vein.Mapper.UserDao;
import com.huantek.vein.Model.UserBean;
import com.huantek.vein.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void insertUser(UserBean userBean) {
        userDao.insertUser(userBean);
    }

    @Override
    public List<UserBean> queryUser() {
        List<UserBean> userBeans = userDao.queryUser();
        return userBeans;
    }

    @Override
    public void updateUser(String text) {
        userDao.updateUser(text);
    }
}
