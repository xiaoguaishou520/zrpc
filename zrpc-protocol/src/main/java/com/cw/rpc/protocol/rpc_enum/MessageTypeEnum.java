package com.cw.rpc.protocol.rpc_enum;

import lombok.Getter;

/**
 * 消息类型枚举类
 */
public enum MessageTypeEnum {

    REQUEST(1),
    RESPOSE(2);

    @Getter
    private int type;

    MessageTypeEnum(int type) {
        this.type = type;
    }

    /**
     * 根据协议头字段的数值，定位到消息类型
     * @param type
     * @return
     */
    public static MessageTypeEnum findByType(int type) {
        for (MessageTypeEnum typeEnum : MessageTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return null;
    }
}
