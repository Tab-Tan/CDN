package cn.awall.awalladmin.dao;

import cn.awall.awalladmin.pojo.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface LikeMapper {

    Like getByLikedUserIdAndLikedPostId(@Param("userId") Long userId, @Param("articleId") Long articleId);

    Integer getCountById(@Param("id") Long id);

    int saveLike(@Param("like") Like like);

    int setStatusById(@Param("id") Long id, @Param("status") Integer status);
}
