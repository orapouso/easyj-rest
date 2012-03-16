package org.easyj.rest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easyj.spring.view.EasyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected <E> BindingResult createBindingResult(Class<E> klazz) {
        return new BeanPropertyBindingResult(klazz, StringUtils.uncapitalize(klazz.getSimpleName()));
    }

    protected ModelAndView configMAV(Object data) {
        return configMAV(data, null);
    }

    protected ModelAndView configMAV(Object data, BindingResult result) {
        ModelAndView mav = new ModelAndView();
        
        mav.addObject(EasyView.PROPERTY_EXCLUSIONS, getExclusions());
        mav.addObject(EasyView.DATA, data);
        if(result != null && result.hasErrors()) {
            mav.addObject(EasyView.BINDING_RESULT, result);
        }

        return mav;
    }

    public Map<Class, List<String>> getExclusions() {
        return new HashMap<Class, List<String>>();
    };

}
