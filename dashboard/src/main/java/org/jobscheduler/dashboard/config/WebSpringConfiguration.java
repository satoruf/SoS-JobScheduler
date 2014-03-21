package org.jobscheduler.dashboard.config;

import javax.servlet.MultipartConfigElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebSpringConfiguration extends WebMvcConfigurerAdapter {

	private static final Logger log = LoggerFactory.getLogger(WebSpringConfiguration.class);
	
	@Autowired
	Environment env;
	
	
	/**
	 * Add swagger
	 * Add JS/HTML managed by bower
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Map all static resources coming to /rest-api-doc/** to the resource files under the 'swagger' directory
		ResourceHandlerRegistration registrationSwagger = registry.addResourceHandler("/rest-api-doc/**");
		registrationSwagger.addResourceLocations("classpath:/rest-api-doc/");

	}
	
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }	


	
	
	
	
}
