package com.mohchi.example.web.framework;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class ExtendedWebMvcConfiguration extends WebMvcConfigurationSupport {

	@Override @Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		ExtendedRequestMappingHanderMapping handlerMapping = new ExtendedRequestMappingHanderMapping();
		handlerMapping.setOrder(0);
		handlerMapping.setInterceptors(getInterceptors());
		return handlerMapping;
	}

	@Override @Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
		adapter.setIgnoreDefaultModelOnRedirect(true);
		return adapter;
	}

}
