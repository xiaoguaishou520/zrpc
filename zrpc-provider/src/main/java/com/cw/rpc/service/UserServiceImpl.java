package com.cw.rpc.service;

import com.cw.rpc.interfaces.UserService;
import com.cw.rpc.annotation.RpcService;

/**
 * @Author 小怪兽
 * @Date 2021-04-01
 */
@RpcService(serviceInterface = UserService.class,version = "1.0.0")
public class UserServiceImpl implements UserService {

    @Override
    public String hello() {
        return "hello,zrpc!!!";
    }
}
