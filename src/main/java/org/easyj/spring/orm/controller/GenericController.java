package org.easyj.spring.orm.controller;

import java.util.List;
import java.util.Map;
import org.easyj.spring.view.EasyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class GenericController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public ModelAndView createModelAndView() {
        return new ModelAndView();
    }

    public <T> BindingResult createBindingResult(Class<T> klazz) {
        return new BeanPropertyBindingResult(klazz, StringUtils.uncapitalize(klazz.getSimpleName()));
    }

    protected void configModelAndView(ModelAndView mav, String retStatus, Object data, String viewName) {
        configModelAndView(mav, retStatus, data, null, viewName);
    }

    protected void configModelAndView(ModelAndView mav, String retStatus, Object data, BindingResult result) {
        configModelAndView(mav, retStatus, data, result, result.getObjectName());
    }

    protected void configModelAndView(ModelAndView mav, String retStatus, Object data, BindingResult result, String viewName) {
        mav.addObject(EasyView.PROPERTY_EXCLUSIONS, getExclusions());

        mav.addObject(EasyView.STATUS, retStatus);
        mav.addObject(EasyView.DATA, data);
        mav.addObject(EasyView.BINDING_RESULT, result);

        mav.setViewName(viewName);
    }

    public Map<Class, List<String>> getExclusions() {
        return null;
    };

}
