package com.resumeassistant.user.dto;

import lombok.Data;

/**
 * 微信授权获取access_token响应
 */
@Data
public class WechatAccessTokenResponse {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    
    // 错误信息
    private Integer errcode;
    private String errmsg;
}
