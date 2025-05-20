package com.resumeassistant.user.controller;

import com.resumeassistant.common.dto.ApiResponse;
import com.resumeassistant.user.dto.AuthResponse;
import com.resumeassistant.user.dto.LoginRequest;
import com.resumeassistant.user.dto.PhoneLoginRequest;
import com.resumeassistant.user.dto.RegisterRequest;
import com.resumeassistant.user.dto.SendSmsCodeRequest;
import com.resumeassistant.user.dto.WechatQrCodeResponse;
import com.resumeassistant.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("用户注册: {}", registerRequest.getUsername());
        return ApiResponse.success(userService.register(registerRequest));
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录: {}", loginRequest.getUsername());
        return ApiResponse.success(userService.login(loginRequest));
    }
    
    /**
     * 获取微信登录二维码
     */
    @GetMapping("/wechat/qrcode")
    public ApiResponse<WechatQrCodeResponse> getWechatQrCode() {
        log.info("获取微信登录二维码, 请求路径: {}", "/v1/auth/wechat/qrcode");
        try {
            WechatQrCodeResponse response = userService.generateWechatQrCode();
            log.info("微信二维码生成成功: {}", response);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("微信二维码生成失败", e);
            return ApiResponse.error(500, "获取微信二维码失败: " + e.getMessage());
        }
    }
    
    /**
     * 微信登录回调
     */
    @GetMapping("/wechat/callback")
    public ApiResponse<AuthResponse> wechatCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        log.info("微信登录回调: code={}, state={}", code, state);
        return ApiResponse.success(userService.wechatCallback(code, state));
    }
    
    /**
     * 发送手机验证码
     */
    @PostMapping("/sms/code")
    public ApiResponse<Boolean> sendSmsCode(@Valid @RequestBody SendSmsCodeRequest request) {
        log.info("发送手机验证码: {}", request.getPhone());
        return ApiResponse.success(userService.sendSmsCode(request));
    }
    
    /**
     * 手机号验证码登录
     */
    @PostMapping("/phone/login")
    public ApiResponse<AuthResponse> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        log.info("手机号登录: {}", request.getPhone());
        return ApiResponse.success(userService.phoneLogin(request));
    }
}
