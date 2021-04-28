package cn.awall.awalladmin.service;

import cn.awall.awalladmin.dto.CountDTO;
import cn.awall.awalladmin.enums.LikedCountStatusEnum;
import cn.awall.awalladmin.enums.LikedStatusEnum;
import cn.awall.awalladmin.pojo.Like;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RedisLikeService {

    /**
     * 是否有点赞记录
     * @param likedUserId
     * @param likedPostId
     * @return
     */
    LikedStatusEnum starStatus(String likedUserId, String likedPostId);

    /**
     * 是否有点赞数量记录
     * @param postId
     * @return
     */
    LikedCountStatusEnum countStatus(String postId);
    /**
     * 点赞，状态1
     * @param likedUserId
     * @param likedPostId
     */
    void saveLikedRedis(String likedUserId, String likedPostId);

    /**
     * 取消点赞，状态0
     * @param likedUserId
     * @param likedPostId
     */
    void unlikeRedis(String likedUserId, String likedPostId);

    /**
     * 删除一条点赞记录
     * @param likedUserId
     * @param likedPostId
     */
    void delLikedRedis(String likedUserId, String likedPostId);

    Integer getCountByIdRedis(String postId);
    /**
     * 增加一个文章点赞数量记录
     * @param postId
     */
    void saveCountRedis(String postId,Integer count);
    /**
     * 删除一条文章点赞数量记录
     * @param postId
     */
    void delCountRedis(String postId);

    /**
     * 文章的点赞数量+1
     * @param postId
     */
    void incrementCount(String postId);

    /**
     * 文章的点赞数量-1
     * @param postId
     */
    void decrementCount(String postId);

    /**
     * 获取所有点赞的数据
     * @return
     */
    List<Like> getLikedDataRedis();

    /**
     * 获取所有文章的点赞数量
     * @return
     */
    List<CountDTO> getCountRedis();

}
