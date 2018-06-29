package com.nowcoder.configuration;

import com.nowcoder.Interceptor.LoginRequiredInterceptor;
import com.nowcoder.Interceptor.PassPortInterceptor;
import org.omg.PortableInterceptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassPortInterceptor passPortInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(passPortInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
