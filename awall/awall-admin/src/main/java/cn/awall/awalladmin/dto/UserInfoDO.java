package cn.awall.awalladmin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDO {

    @JsonProperty("userId")
    private Long id;//用户Id
    private String ac;//认证信息
    private String tel;//手机号码
    private String url;//主页链接
    private String nikename;//昵称
    private String img;//头像

}