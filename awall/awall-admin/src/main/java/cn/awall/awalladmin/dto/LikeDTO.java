package cn.awall.awalladmin.dto;

import cn.awall.awalladmin.enums.LikedStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDTO {
    //用户id
    private String likedUserId;
    //被点赞文章id
    private String likedPostId;
    //点赞的状态
    private Integer status = LikedStatusEnum.UNLIKE.getCode();

}
