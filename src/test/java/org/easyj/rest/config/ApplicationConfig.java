package org.easyj.rest.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ApplicationConfig{
	
	// API

	@Bean
        public static LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
	
}
