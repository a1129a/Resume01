package com.resumeassistant.user.controller;

import com.resumeassistant.common.dto.ApiResponse;
import com.resumeassistant.user.dto.UserDTO;
import com.resumeassistant.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取当前用户信息
     * 注：实际项目中应从请求头中获取用户ID，这里简化处理
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(@RequestHeader("X-User-ID") Long userId) {
        log.info("获取当前用户信息: userId={}", userId);
        return ApiResponse.success(userService.getUserById(userId));
    }
    
    /**
     * 获取指定用户信息
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long userId) {
        log.info("获取用户信息: userId={}", userId);
        return ApiResponse.success(userService.getUserById(userId));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public ApiResponse<UserDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDTO userDTO) {
        log.info("更新用户信息: userId={}", userId);
        return ApiResponse.success(userService.updateUser(userId, userDTO));
    }
}
