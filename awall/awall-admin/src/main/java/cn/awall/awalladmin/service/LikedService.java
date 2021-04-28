package cn.awall.awalladmin.service;

import cn.awall.awalladmin.dto.LikeDTO;
import cn.awall.awalladmin.pojo.Like;
import org.springframework.stereotype.Service;

@Service
public interface LikedService {

    /**
     * 通过id查询点赞数量
     * @param postId
     * @return
     */
    Integer getCountById(String postId);

    /**
     * 通过被点赞人和点赞人id查询是否存在点赞记录
     * @param likedUserId
     * @param likedPostId
     * @return
     */
    Like getByLikedUserIdAndLikedPostId(Long likedUserId, Long likedPostId);

    /**
     * 将Redis里的点赞数据存入数据库中
     */
    void transLikedFromRedis2DB();

    /**
     * 将Redis中的点赞数量数据存入数据库
     */
    void transLikedCountFromRedis2DB();
}
