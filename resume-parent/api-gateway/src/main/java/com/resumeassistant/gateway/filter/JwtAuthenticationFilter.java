package com.resumeassistant.gateway.filter;

import com.resumeassistant.common.constant.CommonConstants;
import com.resumeassistant.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    // 无需token的白名单路径
    private static final List<String> WHITELIST = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/wechat/qrcode",
            "/api/v1/auth/wechat/callback",
            "/api/v1/templates/public"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 如果在白名单中，直接放行
        if (isInWhitelist(path)) {
            return chain.filter(exchange);
        }
        
        // 获取token
        String token = getToken(request);
        
        // 验证token
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange);
        }
        
        try {
            // 验证token，这里简化处理，实际应根据用户名查询数据库验证
            String username = JwtUtil.extractUsername(token);
            if (username == null || !JwtUtil.validateToken(token, username)) {
                return unauthorized(exchange);
            }
            
            // 将用户信息传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-ID", JwtUtil.extractClaim(token, claims -> (String)claims.get("userId")))
                    .header("X-User-Role", JwtUtil.extractClaim(token, claims -> (String)claims.get("role")))
                    .header("X-Username", username)
                    .build();
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return unauthorized(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -100; // 确保在其他过滤器之前执行
    }
    
    /**
     * 判断请求路径是否在白名单中
     */
    private boolean isInWhitelist(String path) {
        return WHITELIST.stream().anyMatch(path::startsWith);
    }
    
    /**
     * 从请求头中获取token
     */
    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(CommonConstants.HEADER_AUTH);
        if (authHeader != null && authHeader.startsWith(CommonConstants.TOKEN_PREFIX)) {
            return authHeader.substring(CommonConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
    
    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
