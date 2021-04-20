package com.cw.rpc.protocol.rpc_enum;

import lombok.Getter;

/**
 * 消息状态枚举类
 */
public enum MessageStatusEnum {

    SUCCESS(1),
    FAIL(2);

    @Getter
    private int type;

    MessageStatusEnum(int type) {
        this.type = type;
    }

}
