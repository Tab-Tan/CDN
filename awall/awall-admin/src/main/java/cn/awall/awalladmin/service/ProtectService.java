package cn.awall.awalladmin.service;

import cn.awall.awalladmin.pojo.Protect;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProtectService {
    //分页查询
    List<Protect> getArticlesByPage(int pageNum, int len);
}
