package com.koali.dao;

import com.koali.pojo.User;

/**
 * Created by Elric on 2017/4/20.
 */
public interface UserDao {
    User selectByName(String username);
}
