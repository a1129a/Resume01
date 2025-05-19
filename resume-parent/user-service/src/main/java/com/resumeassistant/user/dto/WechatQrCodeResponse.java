package com.resumeassistant.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信二维码登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WechatQrCodeResponse {
    
    private String qrCodeUrl;
    private String state;
    private Long expireTime;
}
