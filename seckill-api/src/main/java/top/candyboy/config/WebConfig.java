package top.candyboy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.candyboy.access.AccessInterceptor;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    AccessInterceptor accessInterceptor;

    @Autowired
    public void setAccessInterceptor(AccessInterceptor accessInterceptor) {
        this.accessInterceptor = accessInterceptor;
    }

    public void addInterceptor(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
