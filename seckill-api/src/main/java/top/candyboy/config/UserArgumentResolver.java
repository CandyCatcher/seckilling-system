package top.candyboy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.candyboy.access.UserContext;
import top.candyboy.pojo.User;
import top.candyboy.service.UserService;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    UserService userService;
    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> aClass = methodParameter.getParameterType();
        //判断是不是user类型
        return aClass == User.class;
    }

    //将判断逻辑放在这里面
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
    }
}
