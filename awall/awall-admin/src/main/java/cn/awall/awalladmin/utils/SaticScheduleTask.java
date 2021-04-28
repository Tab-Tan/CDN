package cn.awall.awalladmin.utils;

import cn.awall.awalladmin.dao.ArticleMapper;
import cn.awall.awalladmin.service.LikedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class SaticScheduleTask {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private LikedService likedService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //3.添加定时任务
    @Scheduled(cron = "0/30 * * * * ?")
    private void configureTasks() {

        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());

        //获取此数据库中所有kv
        Set<String> keys = redisTemplate.keys("*_hot_now_dao");
        assert keys != null;
        ArrayList<String> list = new ArrayList<>(keys);
        //千层饼开始了
        for (String s : list) {

            //获取id
            String id = s.split("_")[0];
            //判断id是否失效
            if (!redisUtils.hasKey(id + "_hot_now")) {
                //获取本次产生的热度
                Integer count = (Integer) redisUtils.get(id + "_hot_now_dao");
                //判断是否超过历史热度
                if (count > (Integer) redisUtils.get(id + "_hot_history")) {
                    //如果超过，持久化
                    articleMapper.updateCountById(Long.parseLong(id), count.longValue());
                }
                //删除该id的dao
                redisUtils.del(id + "_hot_now_dao");
                redisUtils.del(id + "_hot_history");
            }
        }

    }

    // 点赞定时任务
    @Scheduled(cron = "0 0/5 * * * *")
    private void likeTask(){
        log.info("持久化点赞数据");
        likedService.transLikedFromRedis2DB();
        likedService.transLikedCountFromRedis2DB();
    }
}












