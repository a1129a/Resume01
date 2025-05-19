package com.resumeassistant.common.constant;

/**
 * 系统常量定义
 */
public class CommonConstants {
    // 通用常量
    public static final String SUCCESS = "操作成功";
    public static final String ERROR = "操作失败";
    
    // 服务名称常量
    public static final String USER_SERVICE = "user-service";
    public static final String RESUME_SERVICE = "resume-service";
    public static final String TEMPLATE_SERVICE = "template-service";
    public static final String AI_SERVICE = "ai-optimization-service";
    public static final String DELIVERY_SERVICE = "delivery-service";
    public static final String NOTIFICATION_SERVICE = "notification-service";
    
    // JWT常量
    public static final String JWT_SECRET_KEY = "resume_assistant_jwt_secret_key_2025";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTH = "Authorization";
    public static final long ACCESS_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 24小时
    
    // 用户相关常量
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // 微信扫码登录常量
    public static final String WECHAT_STATE_PREFIX = "wx_login_state:";
    public static final int WECHAT_QR_EXPIRE_SECONDS = 120; // 二维码过期时间120秒
    
    // Kafka主题常量
    public static final String TOPIC_RESUME_DELIVERY = "resume.delivery";
    public static final String TOPIC_DELIVERY_RESULT = "delivery.result";
    
    // RabbitMQ常量
    public static final String EXCHANGE_NOTIFICATION = "notification.exchange";
    public static final String QUEUE_EMAIL_NOTIFICATION = "email.notification.queue";
    public static final String QUEUE_WECHAT_NOTIFICATION = "wechat.notification.queue";
    public static final String ROUTING_KEY_EMAIL = "notification.email";
    public static final String ROUTING_KEY_WECHAT = "notification.wechat";
}
