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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.easyj.orm.SingleService;
import org.easyj.rest.annotations.EntityValidator;
import org.easyj.rest.annotations.ViewMapping;
import org.springframework.validation.Validator;
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

    private String entityMapping;
    private String baseViewName;
    private String entityViewName = "{}/entity";
    private String editViewName = "{}/form";
    private String createViewName = "{edit}";
    private String listViewName = "{}/list";
    private String postViewName = "";
    private String putViewName = "";
    private String deleteViewName = "";
    private String getViewName = "";
    
    private List<Validator> validators;
    
    @PostConstruct
    public void initialize() {
        initializeViewMapping();
        initializeValidators();
    }
    
    private void initializeViewMapping() {
        if(!getClass().isAnnotationPresent(RequestMapping.class)) return;
        
        entityMapping = getClass().getAnnotation(RequestMapping.class).value()[0];
        
        ViewMapping annon = getClass().getAnnotation(ViewMapping.class);
        if(annon == null) {
            if(getEntityMapping().charAt(0) == '/') {
                baseViewName = getEntityMapping().substring(1);
            }
        } else {
            if(!annon.value().equals("#ROOT")) {
                baseViewName = annon.value();
            } else {
                baseViewName = getEntityMapping().substring(1);
            }
            
            if(!annon.edit().isEmpty()) {
                editViewName = annon.edit();
            }
            
            if(!annon.create().isEmpty()) {
                createViewName = annon.create();
            }
            
            if(!annon.entity().isEmpty()) {
                entityViewName = annon.entity();
            }
            
            if(!annon.list().isEmpty()) {
                listViewName = annon.list();
            }
            
            if(annon.post().isEmpty()) {
                postViewName = "redirect:" + getEntityMapping();
            } else {
                postViewName = annon.post();
            }
            
            if(annon.put().isEmpty()) {
                putViewName = "redirect:" + getEntityMapping() + "/{id}";
            } else {
                putViewName = annon.put();
            }
            
            if(annon.delete().isEmpty()) {
                deleteViewName = "redirect:" + getEntityMapping();
            } else {
                deleteViewName = annon.delete();
            }

        }
        editViewName = getEditViewName().replace("{}", getBaseViewName());
        createViewName = getCreateViewName().replace("{}", getBaseViewName()).replace("{edit}", editViewName);
        entityViewName = getEntityViewName().replace("{}", getBaseViewName());
        listViewName = getListViewName().replace("{}", getBaseViewName());
        
        if(annon != null) {
            if(annon.get().isEmpty()) {
                getViewName = getEntityViewName();
            } else {
                getViewName = annon.get();
            }
        }
        
    }
    
    private void initializeValidators() {
        validators = new ArrayList<Validator>();

        if(!getClass().isAnnotationPresent(EntityValidator.class)) return;
        
        Class<? extends Validator>[] validatorsClasses = getClass().getAnnotation(EntityValidator.class).validators();
        for(Class<? extends Validator> val : validatorsClasses) {
            try {
                validators.add(val.newInstance());
            } catch (Exception ex) {}
        }
    }
    
    public List<Validator> getValidators() {
        return validators;
    }

    public String getEntityMapping() {
        return entityMapping;
    }

    public String getBaseViewName() {
        return baseViewName;
    }

    public String getEntityViewName() {
        return entityViewName;
    }

    public String getEditViewName() {
        return editViewName;
    }

    public String getCreateViewName() {
        return createViewName;
    }

    public String getListViewName() {
        return listViewName;
    }

    public String getPostViewName() {
        return postViewName;
    }

    public String getPutViewName() {
        return putViewName;
    }

    public String getDeleteViewName() {
        return deleteViewName;
    }

    public String getGetViewName() {
        return getViewName;
    }
    
}
