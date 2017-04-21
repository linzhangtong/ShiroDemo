package com.koali.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Elric on 2017/4/20.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @RequestMapping("/admin")
    public String showAdmin(){
        return "alist";
    }
}
