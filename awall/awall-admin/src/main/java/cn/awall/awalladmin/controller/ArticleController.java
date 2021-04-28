package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.pojo.Article;
import cn.awall.awalladmin.pojo.UFile;
import cn.awall.awalladmin.pojo.User;
import cn.awall.awalladmin.service.ArticleService;
import cn.awall.awalladmin.service.FileService;
import cn.awall.awalladmin.service.UserService;
import cn.awall.awalladmin.service.impl.HotServiceImpl;
import cn.awall.awalladmin.vo.CommonResult;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin
@RestController
public class ArticleController {

    @Resource
    private ObjectMapper mapper;
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;

    @Resource
    private HotServiceImpl hotService;


    //分页查询
    @SneakyThrows
    @GetMapping("/awall/articles/{page}/{len}")
    public CommonResult queryByPage(@PathVariable int page, @PathVariable int len) {

        ArrayList<ObjectNode> list = new ArrayList<>();
        List<Article> articles = articleService.getArticlesByPage(page, len);
        for (Article article : articles) {
            //处理文章中的图片
            String imgs = article.getImgs();
            StringBuilder sb = new StringBuilder();
            if (imgs != null && !"".equals(imgs)) {
                String[] ids = imgs.split(",");
                for (int i = 0; i < ids.length; i++) {
                    UFile uFile = fileService.queryById(Long.valueOf(ids[i]));
                    sb.append(uFile.getUrl());
                    if (i < ids.length - 1) {
                        sb.append(",");
                    }
                }
            }
            String urls = sb.toString();
            Long userId = article.getUserId();
            User user = userService.getUser(userId);
            ObjectNode node = mapper.createObjectNode();
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("nikeName", user.getNikename());
            userNode.put("rz", user.getAc());
            userNode.put("userId", user.getUserId());
            userNode.put("url", user.getHeadImg());
            node.put("user", userNode);
            node.put("date", article.getDate().toString());
            node.put("text", article.getContent());
            node.put("imgs", urls);
            node.put("star", article.getStar());
            node.put("show", false);
            node.put("id", article.getArticleId());
            node.put("url", "#/article/" + article.getArticleId());
            list.add(node);
        }
        String jsonRes = mapper.writeValueAsString(list);
        return new CommonResult(200, jsonRes);
    }

    //分页查询
    @SneakyThrows
    @GetMapping("/awall/test/{page}/{len}")
    public Map queryByPageTest(@PathVariable int page, @PathVariable int len, HttpServletRequest request) {

        ArrayList<ObjectNode> list = new ArrayList<>();
        List<Article> articles = articleService.getArticlesByPage(page, len);
        for (Article article : articles) {
            //处理文章中的图片
            String imgs = article.getImgs();
            StringBuilder sb = new StringBuilder();
            if (imgs != null && !"".equals(imgs)) {
                String[] ids = imgs.split(",");
                for (int i = 0; i < ids.length; i++) {
                    UFile uFile = fileService.queryById(Long.valueOf(ids[i]));
                    sb.append(uFile.getUrl());
                    if (i < ids.length - 1) {
                        sb.append(",");
                    }
                }
            }
            String urls = sb.toString();
            Long userId = article.getUserId();
            User user = userService.getUser(userId);
            ObjectNode node = mapper.createObjectNode();
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("nikeName", user.getNikename());
            userNode.put("rz", user.getAc());
            userNode.put("userId", user.getUserId());
            userNode.put("url", user.getHeadImg());
            node.put("user", userNode);
            node.put("date", article.getDate().toString());
            node.put("text", article.getContent());
            node.put("imgs", urls);
            node.put("star", article.getStar());
            node.put("show", false);
            node.put("id", article.getArticleId());
            node.put("url", "#/article/" + article.getArticleId());
            list.add(node);
        }
        String jsonRes = mapper.writeValueAsString(list);
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("pages", articles.size());
        result.put("data", list);
        return result;
    }

    @SneakyThrows
    @GetMapping("/awall/article/{id}")
    public CommonResult<String> queryArticleById(@PathVariable Long id) {

        Article article = articleService.queryArticleById(id);
        Long userId = article.getUserId();
        User user = userService.getUser(userId);
        //处理文章中的图片
        String imgs = article.getImgs();
        StringBuilder sb = new StringBuilder();
        if (imgs != null && !"".equals(imgs)) {
            String[] ids = imgs.split(",");
            for (int i = 0; i < ids.length; i++) {
                UFile uFile = fileService.queryById(Long.valueOf(ids[i]));
                sb.append(uFile.getUrl());
                if (i < ids.length - 1) {
                    sb.append(",");
                }
            }
        }
        String urls = sb.toString();
        ObjectNode userNode = mapper.createObjectNode();
        userNode.put("nikename", user.getNikename());
        userNode.put("userId", user.getUserId());
        userNode.put("rz", user.getAc());
        userNode.put("img", user.getHeadImg());
        userNode.put("url", "");
        ObjectNode aNode = mapper.createObjectNode();
        aNode.put("date", article.getDate().toString());
        aNode.put("text", article.getContent());
        aNode.put("id", article.getArticleId());
        aNode.put("imgs", urls);
        aNode.put("user", userNode);
        if (article != null) {
            return new CommonResult<>(200, mapper.writeValueAsString(aNode));
        }
        return new CommonResult<>(500, "请求错误，没有这篇文章！");
    }

    //发布文章
    @SneakyThrows
    @PostMapping("/awall/articles/pubArticle")
    public CommonResult<String> pubArticle(@RequestBody Object obj, HttpServletRequest request) {

        String userId = request.getSession().getAttribute("userId").toString();
        Map<String, String> map = mapper.readValue(JSON.toJSONString(obj), Map.class);

        Article article = new Article();
        article.setArticleId(null);
        article.setUserId(Long.valueOf(userId));
        article.setClassify(map.get("classify"));
        article.setContent(map.get("text"));
        article.setCount(0L);
        article.setDate(new Date());
        article.setStar(0);
        article.setTag(map.get("tag"));
        article.setUrl("#/article/1");
        article.setImgs(map.get("imgs"));
        int i = articleService.pubArticle(article);
        if (i == 1) {
            return new CommonResult<>(200, "发布成功!");
        }
        return new CommonResult<>(500, "发布失败!");
    }

    @GetMapping("/articles/count/{id}")
    public CommonResult<String> count(@PathVariable Long id) {
        int i = articleService.countAdd(id);
        if (i == 1) {
            return new CommonResult<>(200, "更新成功");
        } else {
            return new CommonResult<>(500, "更新失败");
        }
    }


    //分页查询热度
    @SneakyThrows
    @GetMapping("/awall/hot")
    public Map queryHot() {

        ArrayList<ObjectNode> list = new ArrayList<>();

        Map<String, Integer> hots = hotService.getHot();

        for (Map.Entry<String, Integer> hot : hots.entrySet()) {
            Article article = articleService.queryArticleById(Long.valueOf(hot.getKey()));

            Long userId = article.getUserId();
            User user = userService.getUser(userId);
            ObjectNode node = mapper.createObjectNode();
            ObjectNode userNode = mapper.createObjectNode();
            node.put("text", "#" + article.getContent().substring(0, 10) + "...#");
            node.put("id", article.getArticleId());
            node.put("url", "#/article/" + article.getArticleId());
            node.put("hot", hot.getValue());
            list.add(node);
        }
        if (hots.size() < 10) {
            int a = 10 - hots.size();
            List<Article> articles = articleService.queryArticleByLimit(a);
            for (Article article : articles) {
                //处理文章中的图片
                String imgs = article.getImgs();
                StringBuilder sb = new StringBuilder();
                if (imgs != null && !"".equals(imgs)) {
                    String[] ids = imgs.split(",");
                    for (int i = 0; i < ids.length; i++) {
                        UFile uFile = fileService.queryById(Long.valueOf(ids[i]));
                        sb.append(uFile.getUrl());
                        if (i < ids.length - 1) {
                            sb.append(",");
                        }
                    }
                }
                String urls = sb.toString();
                Long userId = article.getUserId();
                User user = userService.getUser(userId);
                ObjectNode node = mapper.createObjectNode();
                ObjectNode userNode = mapper.createObjectNode();
                String s = article.getContent();
                String str = "#" + s + "...#";
                int n = 10;
                if (s.length() > n) {
                    str = "#" + s.substring(0, n) + "...#";
                }
                node.put("text", str);
                node.put("id", article.getArticleId());
                node.put("url", "#/article/" + article.getArticleId());
                node.put("hot", article.getCount());
                list.add(node);
            }

        }


        String jsonRes = mapper.writeValueAsString(list);
        HashMap<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "");
        result.put("data", list);
        return result;
    }


}
