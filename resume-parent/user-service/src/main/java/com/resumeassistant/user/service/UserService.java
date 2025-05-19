package com.resumeassistant.user.service;

import com.resumeassistant.user.dto.AuthResponse;
import com.resumeassistant.user.dto.LoginRequest;
import com.resumeassistant.user.dto.RegisterRequest;
import com.resumeassistant.user.dto.UserDTO;
import com.resumeassistant.user.dto.WechatQrCodeResponse;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 认证响应，包含token和用户信息
     */
    AuthResponse register(RegisterRequest registerRequest);
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 认证响应，包含token和用户信息
     */
    AuthResponse login(LoginRequest loginRequest);
    
    /**
     * 生成微信扫码登录二维码
     * @return 微信二维码响应
     */
    WechatQrCodeResponse generateWechatQrCode();
    
    /**
     * 微信扫码登录回调
     * @param code 微信授权码
     * @param state 状态码，用于验证请求的合法性
     * @return 认证响应，包含token和用户信息
     */
    AuthResponse wechatCallback(String code, String state);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户DTO
     */
    UserDTO getUserById(Long userId);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户DTO
     */
    UserDTO getUserByUsername(String username);
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param userDTO 用户DTO
     * @return 更新后的用户DTO
     */
    UserDTO updateUser(Long userId, UserDTO userDTO);
}
