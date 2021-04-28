package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.dao.LikeMapper;
import cn.awall.awalladmin.dto.CountDTO;
import cn.awall.awalladmin.pojo.Like;
import cn.awall.awalladmin.service.ArticleService;
import cn.awall.awalladmin.service.LikedService;
import cn.awall.awalladmin.service.RedisLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
@Component
public class LikedServiceImpl implements LikedService {

    @Autowired
    private LikeMapper likeMapper;

    @Resource
    private RedisLikeService redisService;

    @Autowired
    private ArticleService articleService;

    @Override
    public Integer getCountById(String postId) {
        return likeMapper.getCountById(Long.valueOf(postId));
    }

    @Override
    public Like getByLikedUserIdAndLikedPostId(Long likedUserId, Long likedPostId) {
        return likeMapper.getByLikedUserIdAndLikedPostId(likedUserId,likedPostId);
    }

    @Override
    public void transLikedFromRedis2DB() {
        //redis中取所有数据
        List<Like> likeList = redisService.getLikedDataRedis();
        for (Like like : likeList) {
            Like isEx = getByLikedUserIdAndLikedPostId(like.getUserId(), like.getArticleId());
            if (isEx == null){//如果没有记录就存
                likeMapper.saveLike(like);
            }else {//数据库里有记录就去修改状态
                likeMapper.setStatusById(isEx.getLikeId(),like.getStatus());
            }
        }
    }

    @Override
    public void transLikedCountFromRedis2DB() {
        List<CountDTO> countRedis = redisService.getCountRedis();
        for (CountDTO c : countRedis) {//直接更新数据库中文章的点赞数量
            articleService.updateStar(Long.valueOf(c.getKey()),c.getCount());
        }
    }
}
