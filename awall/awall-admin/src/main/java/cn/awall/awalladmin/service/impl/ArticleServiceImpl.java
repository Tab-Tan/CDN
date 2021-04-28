package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.dao.ArticleMapper;
import cn.awall.awalladmin.pojo.Article;
import cn.awall.awalladmin.service.ArticleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ArticleServiceImpl implements ArticleService {

    @Resource
    public ArticleMapper articleMapper;

    @Override
    public int pubArticle(Article article) {
        return articleMapper.addArticle(article);
    }

    @Override
    public List<Article> getArticlesByPage(int pageNum,int len) {
        // 分页
        List<Article> articles = articleMapper.selectByLimit((pageNum-1)*(len+1), len);
        return articles;
    }

    @Override
    public Article queryArticleById(Long id) {
        return articleMapper.queryArticleById(id);
    }

    @Override
    public int countAdd(Long id) {
        return articleMapper.countInc(id);
    }

    @Override
    public void updateStar(Long id, Integer count) {
        articleMapper.updateStar(id, count);
    }
}
