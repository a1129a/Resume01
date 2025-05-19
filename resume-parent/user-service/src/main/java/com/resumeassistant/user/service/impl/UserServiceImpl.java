package com.resumeassistant.user.service.impl;

import com.resumeassistant.common.constant.CommonConstants;
import com.resumeassistant.common.exception.BusinessException;
import com.resumeassistant.common.util.JwtUtil;
import com.resumeassistant.user.dto.*;
import com.resumeassistant.user.entity.User;
import com.resumeassistant.user.repository.UserRepository;
import com.resumeassistant.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    
    /**
     * 用户注册
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // 验证用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw BusinessException.of(400, "用户名已存在");
        }
        
        // 验证邮箱是否已存在（如果有）
        if (StringUtils.hasText(registerRequest.getEmail()) && 
                userRepository.existsByEmail(registerRequest.getEmail())) {
            throw BusinessException.of(400, "邮箱已存在");
        }
        
        // 验证手机号是否已存在（如果有）
        if (StringUtils.hasText(registerRequest.getPhone()) && 
                userRepository.existsByPhone(registerRequest.getPhone())) {
            throw BusinessException.of(400, "手机号已存在");
        }
        
        // 创建用户实体
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .realName(registerRequest.getRealName())
                .role(CommonConstants.ROLE_USER)
                .isEnabled(true)
                .createdTime(LocalDateTime.now())
                .lastLoginTime(LocalDateTime.now())
                .build();
        
        // 保存用户
        user = userRepository.save(user);
        
        // 生成token
        String token = JwtUtil.generateToken(user.getUsername(), user.getId().toString(), user.getRole());
        
        // 构建并返回响应
        return createAuthResponse(user, token);
    }

    /**
     * 用户登录
     */
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // 查找用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> BusinessException.of(400, "用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw BusinessException.of(400, "用户名或密码错误");
        }
        
        // 检查账号是否启用
        if (!user.getIsEnabled()) {
            throw BusinessException.of(400, "账号已被禁用");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成token
        String token = JwtUtil.generateToken(user.getUsername(), user.getId().toString(), user.getRole());
        
        // 构建并返回响应
        return createAuthResponse(user, token);
    }

    /**
     * 生成微信扫码登录二维码
     */
    @Override
    public WechatQrCodeResponse generateWechatQrCode() {
        // 生成唯一状态码
        String state = UUID.randomUUID().toString();
        
        // 模拟生成微信二维码URL（实际项目中应调用微信API）
        String qrCodeUrl = "https://open.weixin.qq.com/connect/qrconnect?" +
                "appid=${wechat.appId}&redirect_uri=${wechat.redirectUrl}" +
                "&response_type=code&scope=snsapi_login&state=" + state;
        
        // 将状态码存入Redis，设置过期时间
        String redisKey = CommonConstants.WECHAT_STATE_PREFIX + state;
        redisTemplate.opsForValue().set(redisKey, "pending", CommonConstants.WECHAT_QR_EXPIRE_SECONDS, TimeUnit.SECONDS);
        
        // 构建并返回响应
        return WechatQrCodeResponse.builder()
                .qrCodeUrl(qrCodeUrl)
                .state(state)
                .expireTime(System.currentTimeMillis() + CommonConstants.WECHAT_QR_EXPIRE_SECONDS * 1000L)
                .build();
    }

    /**
     * 微信扫码登录回调
     */
    @Override
    @Transactional
    public AuthResponse wechatCallback(String code, String state) {
        // 验证状态码是否存在于Redis中
        String redisKey = CommonConstants.WECHAT_STATE_PREFIX + state;
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        if (redisValue == null) {
            throw BusinessException.of(400, "二维码已过期或无效");
        }
        
        // 删除Redis中的状态码
        redisTemplate.delete(redisKey);
        
        // 模拟调用微信API获取用户信息（实际项目中应调用微信API）
        // 这里简化处理，假设已获取到openId和unionId
        String mockOpenId = "mock_openid_" + UUID.randomUUID().toString().substring(0, 8);
        String mockUnionId = "mock_unionid_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 查找用户是否存在
        User user = userRepository.findByWechatOpenId(mockOpenId).orElse(null);
        
        if (user == null) {
            // 创建新用户
            user = User.builder()
                    .username("wx_" + mockOpenId.substring(mockOpenId.length() - 8))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .wechatOpenId(mockOpenId)
                    .wechatUnionId(mockUnionId)
                    .role(CommonConstants.ROLE_USER)
                    .isEnabled(true)
                    .createdTime(LocalDateTime.now())
                    .lastLoginTime(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        } else {
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
        }
        
        // 生成token
        String token = JwtUtil.generateToken(user.getUsername(), user.getId().toString(), user.getRole());
        
        // 构建并返回响应
        return createAuthResponse(user, token);
    }

    /**
     * 根据用户ID获取用户信息
     */
    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.of(404, "用户不存在"));
        return convertToUserDTO(user);
    }

    /**
     * 根据用户名获取用户信息
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.of(404, "用户不存在"));
        return convertToUserDTO(user);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.of(404, "用户不存在"));
        
        // 更新允许修改的字段
        if (StringUtils.hasText(userDTO.getRealName())) {
            user.setRealName(userDTO.getRealName());
        }
        if (StringUtils.hasText(userDTO.getAvatarUrl())) {
            user.setAvatarUrl(userDTO.getAvatarUrl());
        }
        if (StringUtils.hasText(userDTO.getEmail())) {
            // 验证邮箱是否已被其他用户使用
            if (userRepository.existsByEmail(userDTO.getEmail()) && 
                    !userDTO.getEmail().equals(user.getEmail())) {
                throw BusinessException.of(400, "邮箱已存在");
            }
            user.setEmail(userDTO.getEmail());
        }
        if (StringUtils.hasText(userDTO.getPhone())) {
            // 验证手机号是否已被其他用户使用
            if (userRepository.existsByPhone(userDTO.getPhone()) && 
                    !userDTO.getPhone().equals(user.getPhone())) {
                throw BusinessException.of(400, "手机号已存在");
            }
            user.setPhone(userDTO.getPhone());
        }
        
        user = userRepository.save(user);
        return convertToUserDTO(user);
    }
    
    /**
     * 创建认证响应
     */
    private AuthResponse createAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .user(convertToUserDTO(user))
                .build();
    }
    
    /**
     * 将用户实体转换为DTO
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}
