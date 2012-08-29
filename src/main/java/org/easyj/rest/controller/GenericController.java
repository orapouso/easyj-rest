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
import org.easyj.rest.view.EasyView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract controller to provide basic helper methods
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 * @deprecated Use {@code AbstractController} instead
 */
public abstract class GenericController {

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
        if(result.hasErrors()) {
            mav.addObject(EasyView.BINDING_RESULT, result);
        }

        return mav;
    }

    public Map<Class, List<String>> getExclusions() {
        return new HashMap<Class, List<String>>();
    };

}
