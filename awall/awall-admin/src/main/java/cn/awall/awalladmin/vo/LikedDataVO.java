package cn.awall.awalladmin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LikedDataVO {
    private String postId;
    private Integer count;
    private boolean isStar;
}
