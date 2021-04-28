package cn.awall.awalladmin.dao;

import cn.awall.awalladmin.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ArticleMapper {

    //插入
    public int addArticle(Article article);

    //通过文章ID删除
    public int deleteArticleById(Long articleId);

    //修改
    public int updateArticleById(Article article);

    //通过条件查询文章
    public List<Article> selectArticleByArticle(Article article);

    Article queryArticleById(Long id);

    // 分页查询
    List<Article> selectByLimit(int start,int len);

    //获取该文章历史热度值
    public Long queryCountById(String articleId);

    //将历史热度持久化
    public int updateCountById(Long articleId, Long count);

    //浏览量+1
    public int countInc(Long articleId);

    // 修改点赞数量
    void updateStar(@Param("id") Long id, @Param("count") Integer count);

    //按热度查询前n个
    List<Article> queryArticleByLimit(int n);
}
