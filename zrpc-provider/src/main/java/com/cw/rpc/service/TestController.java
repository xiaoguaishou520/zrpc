package com.cw.rpc.service;

import com.cw.rpc.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 */
@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String hello() {
        return userService.hello();
    }
}
