package com.resumeassistant.user.dto;

import lombok.Data;
import java.util.List;

/**
 * 微信用户信息响应
 */
@Data
public class WechatUserInfoResponse {
    private String openid;
    private String nickname;
    private Integer sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;
    private List<String> privilege;
    private String unionid;
    
    // 错误信息
    private Integer errcode;
    private String errmsg;
}
