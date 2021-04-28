package cn.awall.awalladmin.utils;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtils {
    //保存用户点赞数据的key
    public static final String MAP_POST_USER_LIKE = "map_post_user_like";
    //保存用户被点赞数量的key
    public static final String MAP_POST_LIKED_COUNT = "map_post_count_like";
    //保存用户是否登录的key
    public static final String MAP_USER_LOGIN = "map_user_login_login";
    //保存验证码
    public static final String MAP_CODE_LOGIN = "map_code_login";

    /**
     * 拼接被点赞的用户id和点赞的人的id作为key。格式 222222::333333
     * @param likedUserId 被点赞的人id
     * @param likedPostId 点赞的人的id
     * @return
     */
    public static String getLikedKey(String likedUserId, String likedPostId){
        StringBuilder builder = new StringBuilder();
        builder.append(likedUserId);
        builder.append("::");
        builder.append(likedPostId);
        return builder.toString();
    }

    public static String getIsLoginKey(String ip){
        StringBuilder builder = new StringBuilder();
        builder.append(ip);
        return builder.toString();
    }

    public static String getCodeKey(String sessionId){
        StringBuilder builder = new StringBuilder();
        builder.append(sessionId);
        return builder.toString();
    }
}
