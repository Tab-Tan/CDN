package cn.awall.awalladmin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ProtectEnum {
    DISPOSE(1,"已处理"),UNDISPOSED(1,"未处理"),DISPOSING(-1,"处理中"),;
    private Integer code;
    private String msg;
}
