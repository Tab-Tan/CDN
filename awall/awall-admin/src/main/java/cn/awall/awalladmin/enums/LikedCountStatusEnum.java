package cn.awall.awalladmin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LikedCountStatusEnum {

    NULL(-1,"空,需要更新"),
    NOTNULL(1,"不是空"),;
    private Integer code;
    private String msg;
}
