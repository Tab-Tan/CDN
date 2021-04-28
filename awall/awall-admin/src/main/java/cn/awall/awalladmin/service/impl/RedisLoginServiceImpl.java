package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.service.RedisLoginService;
import cn.awall.awalladmin.utils.RedisKeyUtils;
import cn.awall.awalladmin.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisLoginServiceImpl implements RedisLoginService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void setLogin(String ip,String userId) {
        redisUtils.hset(RedisKeyUtils.MAP_USER_LOGIN,RedisKeyUtils.getIsLoginKey(ip),userId);
    }

    @Override
    public void delLogin(String ip) {
        redisUtils.hdel(RedisKeyUtils.MAP_USER_LOGIN,RedisKeyUtils.getIsLoginKey(ip));
    }

    @Override
    public String isLogin(String ip) {
        return  (String)redisUtils.hget(RedisKeyUtils.MAP_USER_LOGIN, RedisKeyUtils.getIsLoginKey(ip));
    }

    @Override
    public void setCode(String sessionId, String code) {
        redisUtils.hset(RedisKeyUtils.MAP_CODE_LOGIN,RedisKeyUtils.getCodeKey(sessionId),code);
    }

    @Override
    public void delCode(String sessionId) {
        redisUtils.hdel(RedisKeyUtils.MAP_CODE_LOGIN,RedisKeyUtils.getCodeKey(sessionId));
    }

    @Override
    public String getCode(String sessionId) {
        return (String) redisUtils.hget(RedisKeyUtils.MAP_CODE_LOGIN,RedisKeyUtils.getCodeKey(sessionId));
    }
}
