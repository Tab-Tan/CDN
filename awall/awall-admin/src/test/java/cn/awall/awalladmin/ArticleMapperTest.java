package cn.awall.awalladmin;

import cn.awall.awalladmin.dao.ArticleMapper;
import cn.awall.awalladmin.pojo.Article;
import cn.awall.awalladmin.utils.RedisKeyUtils;
import cn.awall.awalladmin.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class ArticleMapperTest {

    @Resource
    private ArticleMapper articleMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Test
    public void test1(){
        Article article = new Article();
        article.setArticleId(1L);
        article.setUserId(1L);
        article.setClassify("找对象");
        article.setContent("在这找个对象，女的会Java、linux、前端、网安等等");
        article.setCount(0L);
        article.setDate(new Date());
        article.setStar(0);
        article.setTag("找对象");
        article.setUrl("#/article/1");
        int i = articleMapper.addArticle(article);
        System.out.println(i);
    }

    // 点赞测试
    @Test
    public void test(){
        boolean hset = redisUtils.hset(RedisKeyUtils.MAP_POST_USER_LIKE, "1::3", "0");
        String hget = (String)redisUtils.hget(RedisKeyUtils.MAP_POST_USER_LIKE, "1::2");

        System.out.println("hset:"+hset+",hget:"+hget);
    }
}
