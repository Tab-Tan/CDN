package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.dto.CountDTO;
import cn.awall.awalladmin.enums.LikedCountStatusEnum;
import cn.awall.awalladmin.enums.LikedStatusEnum;
import cn.awall.awalladmin.pojo.Like;
import cn.awall.awalladmin.service.RedisLikeService;
import cn.awall.awalladmin.utils.RedisKeyUtils;
import cn.awall.awalladmin.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RedisServiceImpl implements RedisLikeService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public LikedStatusEnum starStatus(String likedUserId, String likedPostId) {
        String likedKey = RedisKeyUtils.getLikedKey(likedUserId, likedPostId);
        String hget = String.valueOf(redisUtils.hget(RedisKeyUtils.MAP_POST_USER_LIKE, likedKey));
        if ("0".equals(hget)){
            return LikedStatusEnum.UNLIKE;
        }else if("null".equals(hget)){
            return LikedStatusEnum.UNLIKE;
        }else {
            return LikedStatusEnum.LIKE;
        }
    }

    @Override
    public LikedCountStatusEnum countStatus(String postId) {
        String hget = String.valueOf(redisUtils.hget(RedisKeyUtils.MAP_POST_LIKED_COUNT, postId));
        if("null".equals(hget)){
            return LikedCountStatusEnum.NULL;
        }else {
            return LikedCountStatusEnum.NOTNULL;
        }
    }

    @Override
    public void saveLikedRedis(String likedUserId, String likedPostId) {
        String likedKey = RedisKeyUtils.getLikedKey(likedUserId, likedPostId);
        redisUtils.hset(RedisKeyUtils.MAP_POST_USER_LIKE,likedKey, LikedStatusEnum.LIKE.getCode());
    }

    @Override
    public void unlikeRedis(String likedUserId, String likedPostId) {
        String likedKey = RedisKeyUtils.getLikedKey(likedUserId, likedPostId);
        redisUtils.hset(RedisKeyUtils.MAP_POST_USER_LIKE,likedKey,LikedStatusEnum.UNLIKE.getCode());
    }

    @Override
    public void delLikedRedis(String likedUserId, String likedPostId) {
        String likedKey = RedisKeyUtils.getLikedKey(likedUserId, likedPostId);
        redisUtils.hdel(RedisKeyUtils.MAP_POST_USER_LIKE,likedKey);
    }

    @Override
    public Integer getCountByIdRedis(String postId) {
        return (Integer) redisUtils.hget(RedisKeyUtils.MAP_POST_LIKED_COUNT,postId);
    }

    @Override
    public void saveCountRedis(String postId,Integer count) {
        redisUtils.hset(RedisKeyUtils.MAP_POST_LIKED_COUNT,postId,count);
    }

    @Override
    public void delCountRedis(String postId) {
        redisUtils.hdel(RedisKeyUtils.MAP_POST_LIKED_COUNT,postId);
    }

    @Override
    public void incrementCount(String postId) {
        redisUtils.hincr(RedisKeyUtils.MAP_POST_LIKED_COUNT,postId,1);
    }

    @Override
    public void decrementCount(String postId) {
        redisUtils.hdecr(RedisKeyUtils.MAP_POST_LIKED_COUNT,postId,1);
    }

    @Override
    public List<Like> getLikedDataRedis() {
        Map<Object, Object> likes = redisUtils.hmget(RedisKeyUtils.MAP_POST_USER_LIKE);
        Cursor<Map.Entry<Object, Object>> cursor = redisUtils.hscan(RedisKeyUtils.MAP_POST_USER_LIKE);
        ArrayList<Like> likeDTOS = new ArrayList<>();
        while (cursor.hasNext()){
            Map.Entry<Object, Object> entry = cursor.next();
            String key = (String) entry.getKey();
            //userId::PostId
            String[] ids = key.split("::");
            String likedUserId = ids[0];
            String likedPostId = ids[1];
            Integer value =  (Integer)entry.getValue();
            //存到DTO中
            Like like = new Like(null,Long.valueOf(likedUserId), Long.valueOf(likedPostId), value);
            likeDTOS.add(like);

            //存在list后redis删除
            redisUtils.hdel(RedisKeyUtils.MAP_POST_USER_LIKE,key);
        }
        return likeDTOS;
    }

    @Override
    public List<CountDTO> getCountRedis() {
        Cursor<Map.Entry<Object, Object>> hscan = redisUtils.hscan(RedisKeyUtils.MAP_POST_LIKED_COUNT);
        ArrayList<CountDTO> countDTOS = new ArrayList<>();
        while (hscan.hasNext()){
            Map.Entry<Object, Object> entry = hscan.next();
            String key = (String) entry.getKey();
            CountDTO countDTO = new CountDTO(key, ((Integer) entry.getValue()));
            countDTOS.add(countDTO);
            //从redis中删除
            redisUtils.hdel(RedisKeyUtils.MAP_POST_LIKED_COUNT,key);
        }
        return countDTOS;
    }

}
