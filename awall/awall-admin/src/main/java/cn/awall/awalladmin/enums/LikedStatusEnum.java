package cn.awall.awalladmin.enums;

import lombok.Getter;


@Getter
public enum LikedStatusEnum {
    NULL(-1,"未出现数据，需要更新redis"),
    LIKE(1, "点赞"),
    UNLIKE(0, "取消点赞/未点赞"),
    ;

    private Integer code;

    private String msg;

    LikedStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    LikedStatusEnum(){}
}
