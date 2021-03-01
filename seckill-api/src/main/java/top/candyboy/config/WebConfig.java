package top.candyboy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.candyboy.access.AccessInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    UserArgumentResolver userArgumentResolver;
    AccessInterceptor accessInterceptor;
    /*
     框架调用这个方法往controller的参数赋值，遍历方法的参数名称，如果有的话，就将这个参数对应的值设置上
     */
    @Autowired
    private void setUserArgumentResolver(UserArgumentResolver userArgumentResolver) {
        this.userArgumentResolver = userArgumentResolver;
    }
    @Autowired
    public void setAccessInterceptor(AccessInterceptor accessInterceptor) {
        this.accessInterceptor = accessInterceptor;
    }

    //springMvc的controller方法中可以带很多参数，这些参数都是这个方法带入进来的
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    public void addInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
