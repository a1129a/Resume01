package com.resumeassistant.user.service.impl;

import com.resumeassistant.common.constant.CommonConstants;
import com.resumeassistant.common.exception.BusinessException;
import com.resumeassistant.common.util.JwtUtil;
import com.resumeassistant.user.dto.*;
import com.resumeassistant.user.entity.User;
import com.resumeassistant.user.repository.UserRepository;
import com.resumeassistant.user.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
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
    @Value("${wechat.app-id}")
    private String wechatAppId;
    
    @Value("${wechat.redirect-url}")
    private String wechatRedirectUrl;
    
    @Value("${wechat.app-secret}")
    private String wechatAppSecret;
    
    @Value("${wechat.dev-mode:false}")
    private boolean wechatDevMode;
    
    @Override
    public WechatQrCodeResponse generateWechatQrCode() {
        log.info("开始生成微信二维码, 开发模式={}", wechatDevMode);
        
        // 生成唯一状态码
        String state = UUID.randomUUID().toString();
        log.debug("生成状态码: {}", state);
        
        // 检查是否在开发模式
        if (wechatDevMode) {
            log.info("使用开发模式下的模拟微信二维码");
            
            // 将状态码存入Redis，设置过期时间
            String redisKey = CommonConstants.WECHAT_STATE_PREFIX + state;
            log.debug("将状态码存入Redis: key={}, value={}, 过期时间={}s", 
                    redisKey, "pending", CommonConstants.WECHAT_QR_EXPIRE_SECONDS);
            
            redisTemplate.opsForValue().set(redisKey, "pending", CommonConstants.WECHAT_QR_EXPIRE_SECONDS, TimeUnit.SECONDS);
            
            // 构建并返回模拟响应
            WechatQrCodeResponse response = WechatQrCodeResponse.builder()
                    .qrCodeUrl("https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=weixin://wxpay/bizpayurl?dev_mode=true&state=" + state)
                    .state(state)
                    .expireTime(System.currentTimeMillis() + CommonConstants.WECHAT_QR_EXPIRE_SECONDS * 1000L)
                    .build();
                    
            log.info("开发模式微信二维码生成完成: {}", response);
            return response;
        }
        
        // 生产模式下生成微信扫码登录二维码URL
        String encodedRedirectUrl;
        try {
            encodedRedirectUrl = URLEncoder.encode(wechatRedirectUrl, "UTF-8");
            log.debug("编码后的回调URL: {}", encodedRedirectUrl);
        } catch (Exception e) {
            log.error("编码回调URL失败, 原始URL: {}", wechatRedirectUrl, e);
            throw BusinessException.of(500, "生成微信登录二维码失败");
        }
        
        String qrCodeUrl = "https://open.weixin.qq.com/connect/qrconnect?" +
                "appid=" + wechatAppId + 
                "&redirect_uri=" + encodedRedirectUrl + 
                "&response_type=code&scope=snsapi_login&state=" + state;
        
        log.info("生成微信扫码登录URL: {}", qrCodeUrl);
        
        // 将状态码存入Redis，设置过期时间
        String redisKey = CommonConstants.WECHAT_STATE_PREFIX + state;
        log.debug("将状态码存入Redis: key={}, value={}, 过期时间={}s", 
                redisKey, "pending", CommonConstants.WECHAT_QR_EXPIRE_SECONDS);
        
        redisTemplate.opsForValue().set(redisKey, "pending", CommonConstants.WECHAT_QR_EXPIRE_SECONDS, TimeUnit.SECONDS);
        
        // 构建并返回响应
        WechatQrCodeResponse response = WechatQrCodeResponse.builder()
                .qrCodeUrl(qrCodeUrl)
                .state(state)
                .expireTime(System.currentTimeMillis() + CommonConstants.WECHAT_QR_EXPIRE_SECONDS * 1000L)
                .build();
                
        log.info("微信二维码生成完成: {}", response);
        return response;
    }

    private final RestTemplate restTemplate;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                         StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 微信扫码登录回调
     */
    @Override
    @Transactional
    public AuthResponse wechatCallback(String code, String state) {
        log.info("微信回调: code={}, state={}", code, state);
        
        // 验证state是否存在
        String redisKey = CommonConstants.WECHAT_STATE_PREFIX + state;
        String stateValue = redisTemplate.opsForValue().get(redisKey);
        if (stateValue == null) {
            throw BusinessException.of(400, "无效的登录请求");
        }
        
        // 删除Redis中的state
        redisTemplate.delete(redisKey);
        
        try {
            // 开发模式使用模拟数据
            if (wechatDevMode) {
                return handleDevModeWechatLogin(code);
            }
            
            // 生产模式使用真实API
            // 1. 通过code获取access_token
            String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" + 
                    "?appid=" + wechatAppId + 
                    "&secret=" + wechatAppSecret + 
                    "&code=" + code + 
                    "&grant_type=authorization_code";
            
            WechatAccessTokenResponse tokenResponse = restTemplate.getForObject(
                    accessTokenUrl, WechatAccessTokenResponse.class);
            
            if (tokenResponse == null || tokenResponse.getErrcode() != null) {
                log.error("获取微信access_token失败: {}", 
                        tokenResponse != null ? tokenResponse.getErrmsg() : "请求失败");
                throw BusinessException.of(500, "微信登录失败");
            }
            
            String openId = tokenResponse.getOpenid();
            String accessToken = tokenResponse.getAccess_token();
            
            // 2. 获取用户信息
            String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo" + 
                    "?access_token=" + accessToken + 
                    "&openid=" + openId + 
                    "&lang=zh_CN";
            
            WechatUserInfoResponse userInfoResponse = restTemplate.getForObject(
                    userInfoUrl, WechatUserInfoResponse.class);
                    
            if (userInfoResponse == null || userInfoResponse.getErrcode() != null) {
                log.error("获取微信用户信息失败: {}", 
                        userInfoResponse != null ? userInfoResponse.getErrmsg() : "请求失败");
                throw BusinessException.of(500, "微信登录失败");
            }
            
            // 3. 根据openId查找用户
            User user = userRepository.findByWechatOpenId(openId).orElse(null);
            
            if (user == null) {
                // 创建新用户
                user = User.builder()
                        .username("wx_" + openId.substring(Math.max(0, openId.length() - 8)))
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .wechatOpenId(openId)
                        .wechatUnionId(userInfoResponse.getUnionid())
                        .avatarUrl(userInfoResponse.getHeadimgurl())
                        .nickname(userInfoResponse.getNickname())
                        .role(CommonConstants.ROLE_USER)
                        .isEnabled(true)
                        .createdTime(LocalDateTime.now())
                        .lastLoginTime(LocalDateTime.now())
                        .build();
                userRepository.save(user);
            } else {
                // 更新用户信息
                user.setLastLoginTime(LocalDateTime.now());
                if (StringUtils.hasText(userInfoResponse.getHeadimgurl())) {
                    user.setAvatarUrl(userInfoResponse.getHeadimgurl());
                }
                if (StringUtils.hasText(userInfoResponse.getNickname())) {
                    user.setNickname(userInfoResponse.getNickname());
                }
                userRepository.save(user);
            }
            
            // 4. 生成JWT token
            String token = JwtUtil.generateToken(user.getUsername(), user.getId().toString(), user.getRole());
            
            // 5. 返回登录成功响应
            return createAuthResponse(user, token);
            
        } catch (Exception e) {
            log.error("微信登录处理异常", e);
            throw BusinessException.of(500, "微信登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 开发模式下模拟微信登录
     * 用于本地测试时不需要真实微信API
     */
    private AuthResponse handleDevModeWechatLogin(String code) {
        log.info("开发模式下模拟微信登录");
        
        // 生成模拟的微信openId
        String openId = "dev_" + UUID.randomUUID().toString().substring(0, 8);
        
        // 查询用户是否已存在
        User user = userRepository.findByWechatOpenId(openId).orElse(null);
        
        if (user == null) {
            // 创建模拟用户
            user = User.builder()
                    .username("wx_" + openId.substring(Math.max(0, openId.length() - 8)))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .wechatOpenId(openId)
                    .wechatUnionId("dev_union_id")
                    .avatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/dev_avatar/0")
                    .nickname("开发测试用户")
                    .role(CommonConstants.ROLE_USER)
                    .isEnabled(true)
                    .createdTime(LocalDateTime.now())
                    .lastLoginTime(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        } else {
            // 更新登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);
        }
        
        // 生成JWT token
        String token = JwtUtil.generateToken(user.getUsername(), user.getId().toString(), user.getRole());
        
        // 返回登录成功响应
        return createAuthResponse(user, token);
    }

    /**
     * 发送手机验证码
     */
    @Override
    public boolean sendSmsCode(SendSmsCodeRequest request) {
        String phone = request.getPhone();
        log.info("发送手机验证码: {}", phone);
        
        // 生成随机6位验证码
        String code = generateRandomCode(6);
        
        // 存储到Redis，有效期5分钟
        String key = "sms:code:" + phone;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        
        // TODO 调用短信服务发送验证码
        // 测试环境输出验证码
        log.info("发送验证码成功: phone={}, code={}", phone, code);
        
        return true;
    }
    
    /**
     * 手机号验证码登录
     */
    @Override
    @Transactional
    public AuthResponse phoneLogin(PhoneLoginRequest request) {
        String phone = request.getPhone();
        String code = request.getVerifyCode();
        log.info("手机号验证码登录: phone={}", phone);
        
        // 验证验证码
        String key = "sms:code:" + phone;
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode == null || !storedCode.equals(code)) {
            throw BusinessException.of(400, "验证码错误或已失效");
        }
        
        // 验证通过，删除验证码
        redisTemplate.delete(key);
        
        // 判断手机号是否已经注册
        User user = userRepository.findByPhone(phone).orElse(null);
        
        // 新用户，自动注册
        if (user == null) {
            user = User.builder()
                    .username("mobile_" + phone.substring(phone.length() - 4))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .phone(phone)
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
     * 生成随机验证码
     */
    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
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
