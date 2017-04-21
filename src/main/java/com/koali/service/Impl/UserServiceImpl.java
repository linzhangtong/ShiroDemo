package com.koali.service.Impl;

import com.koali.dao.UserDao;
import com.koali.pojo.User;
import com.koali.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Elric on 2017/4/20.
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserDao userDao;
    public User selectUserByNameService(String username) {
        return userDao.selectByName(username);
    }
}
