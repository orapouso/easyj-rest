/*
 *  Copyright 2009-2012 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.easyj.rest.test.config;

import java.util.ArrayList;
import java.util.HashMap;
import org.easyj.rest.view.JSONView;
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
@ComponentScan( { "org.easyj.rest.controller" } )
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
	
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
