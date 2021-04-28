package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.dao.CommentMapper;
import cn.awall.awalladmin.dao.UserMapper;
import cn.awall.awalladmin.dto.CommentDO;
import cn.awall.awalladmin.dto.UserInfoDO;
import cn.awall.awalladmin.pojo.Comment;
import cn.awall.awalladmin.pojo.User;
import cn.awall.awalladmin.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public List<CommentDO> initComments(Long articleId, Integer page, Integer len) {
        ArrayList<CommentDO> list = new ArrayList<>();
        //获取本文章所有顶楼评论
        List<Comment> comments = commentMapper.selectCommentByPage(articleId, (page - 1) * (len + 1), len);
        for (Comment comment : comments) {
            CommentDO commentDO = new CommentDO();
            //评论id
            commentDO.setId(comment.getComId());
            Long userId = comment.getUserId();
            User userById = userMapper.findUserById(userId);
            UserInfoDO userInfoDO = new UserInfoDO();
            userInfoDO.setId(userById.getUserId());
            userInfoDO.setImg(userById.getHeadImg());
            userInfoDO.setNikename(userById.getNikename());
            userInfoDO.setAc(userById.getAc());
            userInfoDO.setUrl("http://localhost:80/userUrl");
            //评论用户信息
            commentDO.setUserInfo(userInfoDO);
            //评论内容
            commentDO.setComment(comment.getContent());
            Long comId = comment.getComId();
            List<Comment> childList = commentMapper.selectCommentByToId(comId);
            ArrayList<CommentDO> commentDOs = new ArrayList<>();
            for (Comment child : childList) {
                User user = userMapper.findUserById(child.getUserId());
                CommentDO aDo = new CommentDO(child.getComId(), new UserInfoDO(user.getUserId(),user.getAc(),user.getTel(),"http://localhost:80/userUrl",user.getNikename(),user.getHeadImg()), child.getContent(), null);
                commentDOs.add(aDo);
            }
            // 楼下所有评论
            commentDO.setChilds(commentDOs);
            list.add(commentDO);
        }

        return list;
    }

    @Override
    public int addComment(Comment comment) {
        return commentMapper.addComment(comment);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentMapper.selectCommentById(id);
    }
}
