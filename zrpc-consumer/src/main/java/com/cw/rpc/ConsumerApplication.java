package com.cw.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author 小怪兽
 * @Date 2021-04-02
 */
@SpringBootApplication(scanBasePackages = "com.cw.rpc")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class,args);
    }
}
