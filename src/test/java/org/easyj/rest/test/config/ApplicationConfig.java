package org.easyj.rest.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ApplicationConfig{
	
	// API

	@Bean
        public static LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
	
}
