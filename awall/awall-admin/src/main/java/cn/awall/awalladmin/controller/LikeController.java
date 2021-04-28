package cn.awall.awalladmin.controller;

import cn.awall.awalladmin.enums.LikedCountStatusEnum;
import cn.awall.awalladmin.enums.LikedStatusEnum;
import cn.awall.awalladmin.service.LikedService;
import cn.awall.awalladmin.service.RedisLikeService;
import cn.awall.awalladmin.service.RedisLoginService;
import cn.awall.awalladmin.utils.IpUtil;
import cn.awall.awalladmin.vo.CommonResult;
import cn.awall.awalladmin.vo.LikedDataVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

@CrossOrigin
@RestController
public class LikeController {

    @Resource
    private RedisLikeService redisLikedService;
    @Resource
    private LikedService likedService;
    @Resource
    private ObjectMapper mapper;



    @SneakyThrows
    @GetMapping("/awall/article/starinit/{userId}/{articleIds}")
    public CommonResult<String> starData(@PathVariable String userId,@PathVariable String articleIds){
        System.out.println("userId:"+userId+",articleIds:"+articleIds);
        String[] ids = articleIds.split(",");
        LikedDataVO[] likedData = new LikedDataVO[ids.length];

        for (int i = 0; i < ids.length; i++) {
            //根据文章id去redis查询当前点赞数量
            LikedCountStatusEnum countStatusEnum = redisLikedService.countStatus(ids[i]);
            if (countStatusEnum.getCode()==-1){//如果缓存未命中就去查数据库，再更新到缓存
                Integer countById = likedService.getCountById(ids[i]);
                redisLikedService.saveCountRedis(ids[i],countById);
            }
            Integer count = redisLikedService.getCountByIdRedis(ids[i]);

            LikedStatusEnum star = redisLikedService.starStatus(userId, ids[i]);
            if(star.getCode()==1){
                LikedDataVO likedDataVO = new LikedDataVO(ids[i], count, true);
                likedData[i] = likedDataVO;
            }else {
                LikedDataVO likedDataVO = new LikedDataVO(ids[i], count, false);
                likedData[i] = likedDataVO;
            }
        }
        if(likedData.length!=0){
            String res = mapper.writeValueAsString(likedData);
            return new CommonResult<>(200,res);
        }
        return new CommonResult<>(500,"请求错误！");
    }
    @GetMapping("/awall/article/star/{userId}/{articleId}")
    public boolean star(@PathVariable String userId,@PathVariable String articleId){
        try {
            LikedStatusEnum statusEnum = redisLikedService.starStatus(userId, articleId);

            if(statusEnum.getCode()==-1){//redis里没记录，插进去，点赞成功
                redisLikedService.saveLikedRedis(userId,articleId);
                redisLikedService.incrementCount(articleId);
                return false;
            } else if(statusEnum.getCode()==1){
                redisLikedService.unlikeRedis(userId,articleId);
                redisLikedService.decrementCount(articleId);
                return false;
            } else {
                redisLikedService.saveLikedRedis(userId,articleId);
                redisLikedService.incrementCount(articleId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SneakyThrows
    @GetMapping("/awall/article/getCount/{ids}")
    public CommonResult<String> getCount(@PathVariable String ids, HttpServletRequest request){

        String[] idList = ids.split(",");

        ArrayList<HashMap> list = new ArrayList<>();
        for (int i = 0; i < idList.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            Integer countByIdRedis = redisLikedService.getCountByIdRedis(idList[i]);

            String userSessionId = (String) request.getSession().getAttribute("userId");
            LikedStatusEnum userId = redisLikedService.starStatus(userSessionId, idList[i]);
            boolean status = false;
            if (userId.getCode()==1){
                status = true;
            }
            map.put("id",idList[i]);
            map.put("value",countByIdRedis==null?0:countByIdRedis);
            map.put("status",status);
            list.add(map);
        }
        String res = mapper.writeValueAsString(list);

        return new CommonResult<>(200,res);
    }
}
