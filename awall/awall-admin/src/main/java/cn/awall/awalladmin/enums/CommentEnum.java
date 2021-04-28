package cn.awall.awalladmin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum CommentEnum {

    COMMENT(-1,"评论"),REPLY(1,"回复"),;
    private Integer code;
    private String msg;
}
