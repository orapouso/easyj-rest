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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.exceptions.ConflictException;
import org.easyj.rest.exceptions.ModelAndViewException;
import org.easyj.spring.view.EasyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract class to provide basic helper methods and to be
 * implemented by other {@code @Controllers}
 *
 * @author Rafael Raposo
 * @since 1.1.0
 */
public abstract class AbstractController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected <E> BindingResult createBindingResult(Class<E> klazz) {
        return new BeanPropertyBindingResult(klazz, StringUtils.uncapitalize(klazz.getSimpleName()));
    }

    protected ModelAndView configMAV(Object data) {
        return configMAV(data, null, null);
    }

    protected ModelAndView configMAV(Object data, BindingResult result) {
        return configMAV(data, result, null);
    }
    
    protected ModelAndView configMAV(Object data, String viewName) {
        return configMAV(data, null, viewName);
    }
    
    protected ModelAndView configMAV(Object data, BindingResult result, String viewName) {
        ModelAndView mav = new ModelAndView();
        
        if(data != null) {
            mav.addObject(EasyView.PROPERTY_EXCLUSIONS, getExclusions());
            mav.addObject(EasyView.DATA, data);
        }
        if(result != null && result.hasErrors()) {
            mav.addObject(EasyView.BINDING_RESULT, result);
        }
        if(viewName != null && !viewName.isEmpty()) {
            mav.setViewName(viewName);
        }

        return mav;
    }
    
    public Map<Class, List<String>> getExclusions() {
        return new HashMap<Class, List<String>>();
    };

    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ModelAndView handleBadRequest(BadRequestException ex) {
        return handleModelAndView(ex);
    }
    
    @ResponseStatus(value=HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ModelAndView handleConflict(ConflictException ex) {
        return handleModelAndView(ex);
    }
    
    private ModelAndView handleModelAndView(ModelAndViewException ex) {
        
        ModelAndView mav = null;

        if(ex.getMav() != null) {
            mav = ex.getMav();
        }
        
        return mav;
    }
    
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatch(TypeMismatchException e) {
        BindingResult result = createBindingResult(getClass());
        
        result.addError(new FieldError("param.bind", e.getRequiredType().getSimpleName(), e.getValue(), true, null, null, "error.param.bind.type"));
        
        return configMAV(null, result, "errors/badrequest");
    }
}
