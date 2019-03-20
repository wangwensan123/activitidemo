package com.tjnetsec.wmo.activiti.controller;

import com.tjnetsec.wmo.activiti.entity.User;
import com.tjnetsec.wmo.activiti.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;



@Controller
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public boolean login(HttpSession session, @RequestBody User user) {
        String userName = user.getUserName();
        String password = user.getPassword();
        boolean login = userService.login(userName, password);
        if (login) {
            session.setAttribute("userName", userName);
            return true;
        }
        return false;
    }

    @GetMapping("/exit")
    public String exit(HttpSession session) {
        session.removeAttribute("userName");
        return "login";
    }
}
