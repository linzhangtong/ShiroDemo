package com.koali.service;

import com.koali.pojo.User;

/**
 * Created by Elric on 2017/4/20.
 */
public interface UserService {
    User selectUserByNameService(String username);
}
