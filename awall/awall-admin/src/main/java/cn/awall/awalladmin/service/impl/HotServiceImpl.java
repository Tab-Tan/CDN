package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.dao.ArticleMapper;
import cn.awall.awalladmin.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HotServiceImpl {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Integer ZAN = 4;//点赞加热度
    private static final Integer ZAN_TIME = 40;//点赞延长失效时间
    private static final Integer PING = 4;//评论加热度
    private static final Integer PING_TIME = 40;//评论延长失效时间
    private static final Integer YD = 2;//阅读加热度
    private static final Integer YD_TIME = 20;//阅读延长失效时间
    private static final Integer INIT_TIME = 100;//初始化失效时间
    private static final Integer N = 2;//比率

    /**
     * 计算存储文章热度
     *
     * @param key  文章id
     * @param type 热度上升类型
     */
    public void heating(String key, String type) {

        //现在热度
        String keyNow = key + "_hot_now";

        //历史热度的key
        String history = key + "_hot_history";

        //现在热度持久化
        String dao = key + "_hot_now_dao";

        //Redis中是否存在该Key
        if (redisUtils.hasKey(keyNow)) {

            //获取当前失效时间
            long expire = redisUtils.getExpire(keyNow);

            //获取当前键值
            Integer v = (Integer) redisUtils.get(keyNow);

            //获取历史热度
            Integer history_hot = (Integer) redisUtils.get(history);

            if (history_hot == null) history_hot = 0;

            //本次比率
            int n = 1;

            //如果当前热度小于历史热度
            if (history_hot > (v / N)) {
                n = N;
            }

            //热度上升
            if (type.equals("zan")) {
                v += (ZAN * n);
                expire += (ZAN_TIME * n);
            } else if (type.equals("ping")) {
                v += (PING * n);
                expire += (PING_TIME * n);
            } else {
                v += (YD * n);
                expire += (YD_TIME * n);
            }

            //写入Redis
            redisUtils.set(keyNow, v, expire);
            redisUtils.set(dao, v);

        } else {

            //去数据库获取该文章热度值
            Long aLong = articleMapper.queryCountById(key);

            //将旧的热度值存储到redis中
            redisUtils.set(history, aLong);

            //将新的热度存储到redis中
            redisUtils.set(keyNow, ZAN, INIT_TIME);
            redisUtils.set(dao, ZAN);

        }

    }

    public Map<String, Integer> getHot() {

        Map<String, Integer> map = new HashMap<String, Integer>();
        //获取所有现在有热度的文章
        Set<String> keys = redisTemplate.keys("*_hot_now");
        ArrayList<String> list = new ArrayList<>(keys);
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).split("_")[0], (Integer) redisUtils.get(list.get(i)));
        }

        LinkedHashMap<String, Integer> sortByValue = sortByValue(map, 0);
        LinkedHashMap<String, Integer> result = subMap((LinkedHashMap<String, Integer>) sortByValue, 10);
        return result;

    }

    /**
     * map根据value排序
     * flag = 1 正序
     * flag = 0 倒序
     *
     * @param map
     * @param flag
     * @return
     */
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map, int flag) {
        LinkedHashMap<K, V> sortMap = new LinkedHashMap<>();
        if (flag == 1) {
            map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).forEach(entry -> sortMap.put(entry.getKey(), entry.getValue()));
        } else {
            map.entrySet().stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())).forEach(entry -> sortMap.put(entry.getKey(), entry.getValue()));
        }
        return sortMap;
    }

    /**
     * 取出map前n个
     *
     * @param map
     * @param length
     * @return
     */
    public LinkedHashMap<String, Integer> subMap(LinkedHashMap<String, Integer> map, int length) {
        List<Map.Entry<String, Integer>> lists = new ArrayList<>(map.entrySet());
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        if (lists.size() >= length) {
            for (Map.Entry<String, Integer> set : lists.subList(0, length)) {
                sortedMap.put(set.getKey(), set.getValue());
            }
        } else {
            for (Map.Entry<String, Integer> set : lists) {
                sortedMap.put(set.getKey(), set.getValue());
            }
        }
        return sortedMap;
    }

}