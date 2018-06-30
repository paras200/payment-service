package com.coinxlab.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class OriginMVCConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private LoggerInterceptor logIntercept;
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logIntercept);
         // .addPathPatterns("*");
    }
}
