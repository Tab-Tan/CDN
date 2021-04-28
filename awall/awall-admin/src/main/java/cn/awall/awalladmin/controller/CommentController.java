package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.dto.CommentDO;
import cn.awall.awalladmin.enums.CommentEnum;
import cn.awall.awalladmin.pojo.Comment;
import cn.awall.awalladmin.pojo.User;
import cn.awall.awalladmin.service.CommentService;
import cn.awall.awalladmin.service.UserService;
import cn.awall.awalladmin.vo.CommonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class CommentController {

    @Resource
    private ObjectMapper mapper;
    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;

    @SneakyThrows
    @GetMapping("/awall/comment/init/{articleId}/{page}/{len}")
    public CommonResult<String> initComment(@PathVariable Long articleId,@PathVariable Integer page,@PathVariable Integer len){
        List<CommentDO> commentDOS = commentService.initComments(articleId, page, len);
        String res = mapper.writeValueAsString(commentDOS);
        System.out.println("res:"+res);
        return new CommonResult<>(200,res);
    }

    @SneakyThrows
    @PostMapping("/awall/comment")
    public CommonResult<String> comment(@RequestBody String data){

        Map<String,Object> map = mapper.readValue(data, Map.class);
        String type = (String) map.get("type");
        Comment comment = new Comment();
        comment.setComId(Long.valueOf((String) map.get("articleId")));
        comment.setDate(new Date());
        comment.setArticleId(Long.valueOf((String)map.get("articleId")));
        comment.setUserId(Long.valueOf(String.valueOf(map.get("userId"))));
        if ("comment".equals(type)){
            comment.setContent((String) map.get("content"));
            comment.setToId(Long.valueOf(CommentEnum.COMMENT.getCode()));
        }else {
            String toId = String.valueOf(map.get("toId"));
            Long aLong = Long.valueOf(toId);
            User user = userService.getUser(aLong);
            comment.setContent("@"+user.getNikename()+" "+map.get("content"));
            String commentId = String.valueOf(map.get("commentId"));
            Long bLong = Long.valueOf(commentId);
            Comment commentById = commentService.getCommentById(bLong);
            Long toIdTemp = null;
            if(commentById.getToId()!=-1){
                 toIdTemp = commentById.getToId();
            }else {
                toIdTemp = bLong;
            }
            comment.setToId(toIdTemp);
        }
        int i = commentService.addComment(comment);

        if (i == 1){
            return new CommonResult<>(200,"评论成功");
        }

        return new CommonResult<>(500,"评论失败");
    }
}
