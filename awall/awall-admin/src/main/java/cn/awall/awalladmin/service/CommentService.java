package cn.awall.awalladmin.service;

import cn.awall.awalladmin.dto.CommentDO;
import cn.awall.awalladmin.pojo.Article;
import cn.awall.awalladmin.pojo.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    List<CommentDO> initComments(Long articleId, Integer page, Integer len);

    int addComment(Comment comment);

    Comment getCommentById(Long id);
}
