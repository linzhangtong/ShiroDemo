package com.koali.service;

import com.koali.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Elric on 2017/4/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-*.xml")
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Test
    public void selectUserByNameService() throws Exception {
        User user = userService.selectUserByNameService("露娜");
        System.out.println(user.toString());
    }
}