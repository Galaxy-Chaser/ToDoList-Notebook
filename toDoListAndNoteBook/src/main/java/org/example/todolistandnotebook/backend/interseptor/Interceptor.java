package org.example.todolistandnotebook.backend.interseptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.todolistandnotebook.backend.service.UsersServiceImpl;
import org.example.todolistandnotebook.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class Interceptor implements HandlerInterceptor {

    @Autowired
    private UsersServiceImpl usersServiceImpl;

    /**
     * 对用户带来的token进行验证，验证失败则不予放行
     * @param request 当前 HTTP 请求对象，封装了客户端发起的请求的所有信息
     * @param response HTTP 响应对象，用于向客户端返回数据或控制响应行为。
     * @param handler 当前请求的处理器对象，通常是 Spring MVC 中处理请求的控制器方法（Controller Method）。
     * @return 返回值为true则表示通过认证，为false则jwt令牌出现问题
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从http请求对象中获取token
        String token = request.getHeader("authorization");

        //判断token是否为空
        if (!StringUtils.hasLength(token)) {
            log.info("token is empty");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("无 Token，请重新登录");
            return false;
        }

        //去除token字符串前多余的部分（在前端中添加）
        token = token.substring(7);

        //解析jwt令牌
        JwtUtils jwtUtils = new JwtUtils();

        //获取令牌存储的内容，如果令牌过期则会抛出异常
        try {
            Claims claims = jwtUtils.ParseJwt(token);

            //判断令牌内容是否为空
            if (claims.get("username") == null) {
                log.info("username is null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("无效 Token");
                return false;
            }

        } catch (Exception e) {
            log.info("验证失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token验证失败: " + e.getMessage());
            return false;
        }

        //满足所有条件，通过验证
        return true;
    }

//    @Override
        // 目标方法执行后执行
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//
//    }
}
