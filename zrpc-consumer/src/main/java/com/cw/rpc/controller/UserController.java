package com.cw.rpc.controller;

import com.cw.rpc.annotation.RpcReference;
import com.cw.rpc.interfaces.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 */
@RestController
public class UserController {

    @RpcReference(registryType = "ZOOKEEPER",registryAddr = "42.193.144.43:2181",timeout = 5000)
    private UserService userService;

    @GetMapping
    public String hello() {
        return userService.hello();
    }
}
