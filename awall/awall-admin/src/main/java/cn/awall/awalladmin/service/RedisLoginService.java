package cn.awall.awalladmin.service;

import org.springframework.stereotype.Service;

@Service
public interface RedisLoginService {


    void setLogin(String ip,String userId);

    void delLogin(String ip);

    String isLogin(String ip);

    void setCode(String sessionId,String code);

    void delCode(String sessionId);

    String getCode(String sessionId);
}
