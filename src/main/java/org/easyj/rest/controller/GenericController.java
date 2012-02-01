package org.easyj.rest.controller;

import java.util.List;
import java.util.Map;
import org.easyj.spring.view.EasyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public abstract class GenericController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public ModelAndView createModelAndView() {
        return new ModelAndView();
    }

    protected <E> BindingResult createBindingResult(Class<E> klazz) {
        return new BeanPropertyBindingResult(klazz, StringUtils.uncapitalize(klazz.getSimpleName()));
    }

    protected ModelAndView configMAV(ModelAndView mav, String retStatus, Object data, String viewName) {
        return configMAV(mav, retStatus, data, null, viewName);
    }

    protected ModelAndView configMAV(ModelAndView mav, String retStatus, Object data, BindingResult result) {
        return configMAV(mav, retStatus, data, result, result.getObjectName());
    }

    protected ModelAndView configMAV(ModelAndView mav, String retStatus, Object data, BindingResult result, String viewName) {
        mav.addObject(EasyView.PROPERTY_EXCLUSIONS, getExclusions());

        mav.addObject(EasyView.STATUS, retStatus);
        mav.addObject(EasyView.DATA, data);
        mav.addObject(EasyView.BINDING_RESULT, result);

        mav.setViewName(viewName);
        
        return mav;
    }

    public Map<Class, List<String>> getExclusions() {
        return null;
    };

}
