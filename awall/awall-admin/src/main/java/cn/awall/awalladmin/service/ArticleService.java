package cn.awall.awalladmin.service;

import cn.awall.awalladmin.pojo.Article;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ArticleService {
    //发布文章
    int pubArticle(Article article);
    //分页查询
    List<Article> getArticlesByPage(int pageNum,int len);
    // 通过id查询
    Article queryArticleById(Long id);
    // 增加浏览量
    int countAdd(Long id);
    // 修改点赞数量
    void updateStar(Long id, Integer count);


    //按热度查询前n个
    List<Article> queryArticleByLimit(int n);

}
