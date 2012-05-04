package org.easyj.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.easyj.rest.exceptions.BadRequestException;
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
        
        ModelAndView mav = null;
        
        if(ex.getResult() != null) {
            mav = configMAV(null, ex.getResult());
        }
        
        return mav;
    }
    
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatch(TypeMismatchException e, HttpServletRequest request) {
        BindingResult result = createBindingResult(getClass());
        
        result.addError(new FieldError("param.bind", e.getRequiredType().getSimpleName(), e.getValue(), true, null, null, "error.param.bind.type"));
        
        return configMAV(null, result);
    }
}
