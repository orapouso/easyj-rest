package org.easyj.rest.test.config;

import org.easyj.orm.jpa.SingleJPAEntityService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceJPAConfig {

        //Mocking service layer for injection
        @Bean
        public SingleJPAEntityService singleJPAEntityService() {
            return Mockito.mock(SingleJPAEntityService.class);
        }
        
}
