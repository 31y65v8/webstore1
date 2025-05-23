package com.wxl.webstore.common.config;

import com.wxl.webstore.common.filter.JwtAuthenticationFilter;

import java.util.Arrays;
import org.springframework.core.Ordered; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.wxl.webstore.common.utils.JwtUtil;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // 密码加密配置
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   


    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register", "/user/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

     */
    // 认证管理器
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    // Spring Security 配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 添加CORS配置
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问的接口
                        .requestMatchers(
                            "/",
                            "/index.html",
                            "/assets/**",
                            "/favicon.ico",
                            "/index",
                            "/api/user/register",
                            "/api/user/login",
                            "/api/product/products",
                            "/api/product/products/category",
                            "/api/product/products/search",
                            "/api/product/upload/image",
                            "/api/product/images/**",
                            "/api/user/info",
                            "/error"  // 添加错误页面路径
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 商家相关接口
                        .requestMatchers("/api/seller/**").hasRole("SELLER")
                        // 客户相关接口
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                        // 管理员相关接口
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(userDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);
                
        
        return http.build();
    }

    // CORS 配置
    /*@Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // 允许前端的域名
        configuration.addAllowedMethod("*"); // 允许所有 HTTP 方法
        configuration.addAllowedHeader("*"); // 允许所有请求头
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 配置全局 CORS
        return source;
    }*/

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 修改点1：允许同源访问（因为前后端已合并部署）
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");  // 替代原来的addAllowedOrigin
        // 修改点2：明确生产环境需要的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 修改点3：设置响应头可见性
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
    
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // 只对API路径生效
        return source;
    }
}