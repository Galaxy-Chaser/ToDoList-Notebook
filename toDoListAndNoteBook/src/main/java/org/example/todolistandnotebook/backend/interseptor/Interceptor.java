package org.example.todolistandnotebook.backend.interseptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.service.UsersService;
import org.example.todolistandnotebook.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class Interceptor implements HandlerInterceptor {

    @Autowired
    private UsersService usersService;

    @Override //目标方法执行前执行，可用于校验Jwt令牌
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (!StringUtils.hasLength(token)) {
            log.info("token is empty");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("无 Token，请重新登录");
            return false;
        }
        token = token.substring(7);
        JwtUtil jwtUtil = new JwtUtil();
        try {
            Claims claims = jwtUtil.ParseJwt(token);
            if (claims.get("username") == null) {
                log.info("username is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("无效 Token");
                return false;
            }
//            request.setAttribute("username", claims.get("username"));
        } catch (Exception e) {
            log.info("验证失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token验证失败: " + e.getMessage());
            return false;
        }
        return true;
    }

//    @Override //目标方法执行后执行
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//
//    }
}
