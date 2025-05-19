package com.resumeassistant.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(name = "wechat_open_id")
    private String wechatOpenId;
    
    @Column(name = "wechat_union_id")
    private String wechatUnionId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "real_name")
    private String realName;
    
    @Column(name = "nickname")
    private String nickname;

    private String role;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;
}
