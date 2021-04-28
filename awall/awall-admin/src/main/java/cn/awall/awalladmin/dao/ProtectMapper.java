package cn.awall.awalladmin.dao;

import cn.awall.awalladmin.pojo.Protect;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProtectMapper {

    //插入数据
    int addProtect(Protect protect);

    //通过id删除数据
    int deleteProtectById(Long protectId);

    //修改数据
    int updateProtect(Protect protect);

    //通过条件查询数据
    List<Protect> selectProtect(Protect protect);

    List<Protect> selectProtectByPage(int start,int len);

}
