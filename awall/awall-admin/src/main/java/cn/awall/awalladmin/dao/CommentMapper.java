package cn.awall.awalladmin.dao;

import cn.awall.awalladmin.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
@Mapper
public interface CommentMapper {

    //插入
    int addComment(Comment comment);

    //通过id删除评论
    int deleteCommentById(Long comId);

    Comment selectCommentById(@Param("id") Long id);

    //通过id修改数据
    int updateCommentById(Comment comment);

    //通过条件查询数据
    List<Comment> selectCommentByComment(Comment comment);
    // 查询每一楼的评论
    List<Comment> selectCommentByPage(@Param("articleId") Long articleId,@Param("start") Integer start,@Param("len") Integer len);
    //查询子评论
    List<Comment> selectCommentByToId(@Param("id") Long id);

}
