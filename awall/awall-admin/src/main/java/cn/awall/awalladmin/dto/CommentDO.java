package cn.awall.awalladmin.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDO {

    private Long id;
    @JsonProperty("user")
    private UserInfoDO userInfo;//用户信息
    private String comment;//评论内容
    private List<CommentDO> childs;//子节点
}
