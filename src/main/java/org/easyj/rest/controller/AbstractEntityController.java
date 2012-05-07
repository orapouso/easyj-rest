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

package org.easyj.rest.controller;

import javax.annotation.PostConstruct;
import org.easyj.orm.SingleService;
import org.easyj.rest.annotations.ViewMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Abstract class to be implemented by other {@code @Entity @Controllers}.
 * <br><br>
 * Implementing classes should provide a concrete {@code SingleService} implementation
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
public abstract class AbstractEntityController extends AbstractController {

    protected SingleService service;

    public abstract void setService(SingleService service);

    public abstract SingleService getService();

    protected String entityMapping;
    protected String baseViewName;
    protected String entityViewName = "{}/entity";
    protected String formViewName = "{}/form";
    protected String listViewName = "{}/list";
    
    @PostConstruct
    public void initialize() {
        entityMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        
        ViewMapping annon = this.getClass().getAnnotation(ViewMapping.class);
        if(annon == null) {
            if(entityMapping.charAt(0) == '/') {
                baseViewName = entityMapping.substring(1);
            }
        } else {
            if(!annon.value().isEmpty()) {
                baseViewName = annon.value();
            }
            if(!annon.form().isEmpty()) {
                formViewName = annon.form();
            }
            if(!annon.entity().isEmpty()) {
                entityViewName = annon.entity();
            }
            if(!annon.list().isEmpty()) {
                listViewName = annon.list();
            }
        }
        formViewName = formViewName.replace("{}", baseViewName);
        entityViewName = entityViewName.replace("{}", baseViewName);
        listViewName = listViewName.replace("{}", baseViewName);
    }
    
}
