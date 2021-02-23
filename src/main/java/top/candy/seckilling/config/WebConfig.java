package top.candy.seckilling.config;

import org.aopalliance.intercept.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import top.candy.seckilling.access.AccessInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    UserArgumentResolver userArgumentResolver;
    AccessInterceptor accessInterceptor;
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
