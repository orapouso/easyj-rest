package org.easyj.rest.test.config;

import java.util.ArrayList;
import java.util.HashMap;
import org.easyj.spring.view.JSONView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@ComponentScan( { "org.easyj.rest" } )
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
	
    public WebConfig(){
            super();
    }

    // beans
	
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver b = new InternalResourceViewResolver();
        b.setSuffix(".jsp");
        b.setPrefix("/WEB-INF/view/");
        b.setViewClass(JstlView.class);
        b.setExposedContextBeanNames(new String[]{"config"});
        return b;
    }

    @Bean
    public JSONView jsonView() {
        JSONView v = new JSONView();
        
        v.setEncoding("ISO-8859-1");
        v.setDateFormat("yyyy-MM-dd HH:mm:ss");
        v.setPreventCache(true);
        
        return v;
    }
    
    
    @Bean
    public ContentNegotiatingViewResolver viewResolver() {
        ContentNegotiatingViewResolver v = new ContentNegotiatingViewResolver();
        
        v.setMediaTypes(new HashMap<String, String>(){{
            put("html", "text/html");
            put("json", "application/json");
        }});
        
        v.setViewResolvers(new ArrayList<ViewResolver>(){{
            add(internalResourceViewResolver());
        }});
        
        v.setDefaultViews(new ArrayList<View>(){{
            add(jsonView());
        }});
        
        return v;
    }
}
