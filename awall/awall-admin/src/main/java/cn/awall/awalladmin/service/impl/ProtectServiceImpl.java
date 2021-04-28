package cn.awall.awalladmin.service.impl;

import cn.awall.awalladmin.dao.ProtectMapper;
import cn.awall.awalladmin.pojo.Protect;
import cn.awall.awalladmin.service.ProtectService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ProtectServiceImpl implements ProtectService {

    @Resource
    private ProtectMapper protectMapper;
    @Override
    public List<Protect> getArticlesByPage(int pageNum, int len) {
        return protectMapper.selectProtectByPage((pageNum-1)*(len+1), len);
    }
}
