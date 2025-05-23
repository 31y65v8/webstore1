package com.wxl.webstore.common.filter;

import com.wxl.webstore.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import org.springframework.lang.NonNull;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                String path = request.getRequestURI();

    // 处理跨域预检请求
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        filterChain.doFilter(request, response);
        return;
    }

    // 跳过登录和注册接口
    if (path.equals("/api/user/login") || path.equals("/api/user/register")||path.equals("/")||path.equals("/index")||path.equals("/api/product/products")||path.equals("/api/product/products/category")||path.equals("/api/product/products/search")||path.equals("/api/product/upload/image")) {
        filterChain.doFilter(request, response);
        return;
    }
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
            try {
                Claims claims = jwtUtil.getClaimsFromToken(token);
                String account = claims.getSubject();
                String role = claims.get("role", String.class);
                Long userId = claims.get("userId", Long.class);

                // 直接创建认证信息，不再查询数据库
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    account, // principal
                    token,  // credentials (原始token)
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );
                
                // 设置额外信息
                authentication.setDetails(new HashMap<String, Object>() {{
                    put("userId", userId);
                    put("role", role);
                }});
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 失效或无效");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}