package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.enums.ProtectEnum;
import cn.awall.awalladmin.pojo.Article;
import cn.awall.awalladmin.pojo.Protect;
import cn.awall.awalladmin.pojo.UFile;
import cn.awall.awalladmin.pojo.User;
import cn.awall.awalladmin.service.ArticleService;
import cn.awall.awalladmin.service.FileService;
import cn.awall.awalladmin.service.ProtectService;
import cn.awall.awalladmin.service.UserService;
import cn.awall.awalladmin.vo.CommonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class ProtectController {
    @Resource
    private ObjectMapper mapper;
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private ProtectService protectService;

    //分页查询
    @SneakyThrows
    @GetMapping("/awall/ex/{page}/{len}")
    public Map queryByPageTest(@PathVariable int page, @PathVariable int len, HttpServletRequest request){

        ArrayList<ObjectNode> list = new ArrayList<>();
        List<Protect> protects = protectService.getArticlesByPage(page, len);

        for (Protect protect : protects) {
            Article article = articleService.queryArticleById(protect.getArticleId());
            //处理文章中的图片
            String imgs = article.getImgs();
            StringBuilder sb = new StringBuilder();
            if (imgs!= null&&!"".equals(imgs)){
                String[] ids = imgs.split(",");
                for (int i=0;i < ids.length; i++) {
                    UFile uFile = fileService.queryById(Long.valueOf(ids[i]));
                    sb.append(uFile.getUrl());
                    if (i<ids.length-1){
                        sb.append(",");
                    }
                }
            }
            String urls = sb.toString();
            Long userId = article.getUserId();
            User user = userService.getUser(userId);
            ObjectNode node = mapper.createObjectNode();
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("nikeName",user.getNikename());
            userNode.put("rz",user.getAc());
            userNode.put("userId",user.getUserId());
            userNode.put("url",user.getHeadImg());
            node.put("user",userNode);
            node.put("date",article.getDate().toString());
            node.put("title",protect.getTitle());
            String status = null;
            switch (protect.getStatus()){
                case "1":status = ProtectEnum.DISPOSE.getMsg();break;
                case "0":status = ProtectEnum.UNDISPOSED.getMsg();break;
                case "-1":status = ProtectEnum.DISPOSING.getMsg();break;
            }
            node.put("status",status);
            node.put("star",article.getStar());
            node.put("show",false);
            node.put("id",protect.getProtectId());
            node.put("url","#/article/"+protect.getArticleId());
            list.add(node);
        }
        String jsonRes = mapper.writeValueAsString(list);
        HashMap<String, Object> result=new HashMap<>();
        result.put("code", 0);
        result.put("msg","");
        result.put("pages", protects.size());
        result.put("data", list);
        return result;
    }
}
