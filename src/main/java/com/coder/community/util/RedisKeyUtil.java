package com.coder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PRIFIX_USER_LIKE = "like:user";
    private static final String PRIFIX_FOLLOWER = "follower";
    private static final String PRIFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "";
    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId);
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个用户的赞
    //like:user:userId - > int
    public static String getUserLikeKey(int userId){
        return PRIFIX_USER_LIKE + SPLIT + userId;
    }

    //某个用户关注的实体
    // followee:userId:entityType  -> zet(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return PRIFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //某个实体拥有的粉丝
    // followee:entityType : entityId -> (zset(userId,now)
    public static String getFollowerKey(int entityTye, int entityId){
        return PRIFIX_FOLLOWER + SPLIT + entityTye + SPLIT + entityId;
    }

    //登录验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录的凭证
    public static String getTicketKey(String ticket){return PREFIX_TICKET + SPLIT + ticket;}

    //用户
    public static String getUserKey(int userId){
        return  PREFIX_USER + SPLIT + userId;
    }
}
