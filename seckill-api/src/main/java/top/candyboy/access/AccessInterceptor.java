package top.candyboy.access;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.candyboy.pojo.User;
import top.candyboy.redis.key.AccessKey;
import top.candyboy.result.CodeMsg;
import top.candyboy.result.Result;
import top.candyboy.redis.RedisOperation;
import top.candyboy.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor implements HandlerInterceptor {
    UserService userService;
    RedisOperation redisOperation;

    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setRedisOperation(RedisOperation redisOperation) {
        this.redisOperation = redisOperation;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            //将用户取出来之后，在其他地方需要验证，所以需要把用户保存起来。使用ThreadLocal
            User user = getUser(request, response);
            UserContext.setUser(user);

            //访问的注解
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            //对于登录功能使用拦截器最好
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    //给客户端提示？
                    render(response, CodeMsg.SERVER_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            }
            Integer count = redisOperation.get(AccessKey.withExpire(seconds), key, Integer.class);
            if (count == null) {
                redisOperation.set(AccessKey.withExpire(seconds), key, 1);
            } else if (count < maxCount) {
                redisOperation.incr(AccessKey.withExpire(seconds), key);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream outputStream = response.getOutputStream();
        String s = JSON.toJSONString(Result.error(codeMsg));
        outputStream.write(s.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String parameterToken = request.getParameter(UserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, UserService.COOKIE_NAME_TOKEN);
        //参数判断
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(parameterToken)) {
            return null;
        }
        //当用户登陆之后，操作其他页面的时候还需要进行判断，繁琐
        String token = StringUtils.isEmpty(parameterToken)? cookieToken:parameterToken;
        return userService.getUserByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
