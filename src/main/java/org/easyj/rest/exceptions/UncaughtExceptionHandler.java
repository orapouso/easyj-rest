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

package org.easyj.rest.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handler to treat uncaught exceptions
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 */
public class UncaughtExceptionHandler implements HandlerExceptionResolver {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String ERROR_STATUS = "error.uncaught";

    private static final String VIEW_NAME = "exception";

    private String viewName;

    public UncaughtExceptionHandler() {
        this.viewName = VIEW_NAME;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mav = new ModelAndView();

        String exception = "";
        Exception ex2 = ex;
        while(ex2 != null) {
            exception += "\n" + ex2.getClass().getSimpleName() + " - " + ex2.getMessage();
            ex2 = (Exception) ex2.getCause();
        }

        mav.setViewName(viewName);
        mav.addObject("status", UncaughtExceptionHandler.ERROR_STATUS);
        mav.addObject("exception", exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        logger.error("Uncaught Exception Error", ex);

        return mav;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

}
