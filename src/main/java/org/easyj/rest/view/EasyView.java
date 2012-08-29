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

package org.easyj.rest.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.AbstractView;

/**
 * View that can be used to make sure that errors and property exclusions are used
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 */
public abstract class EasyView extends AbstractView {

    public static final String PROPERTY_EXCLUSIONS = "propertyExclusions";
    public static final String ERRORS = "errors";
    public static final String BINDING_RESULT = "result";
    public static final String STATUS = "status";
    public static final String DATA = "data";

    private String encoding;
    private Boolean preventCache;
    private String viewName;
    private Map<Class, List<String>> propExclusions;
    private BindingResult result;

    public EasyView() {
        super();
        setContentType("text/html");
        if(encoding == null) setEncoding("UTF-8");
        if(preventCache == null) setPreventCache(true);
    }

    protected void setUp(HttpServletResponse response, Map<String, Object> model) {
        setUpResponse(response);
        filterModel(model);
        renderErrors();
    }

    protected void setUpResponse(HttpServletResponse response) {
        response.setContentType(getContentType());
        response.setCharacterEncoding(getEncoding());
        if(isPreventCache()) {
            responsePreventCache(response);
        }
    }

    @SuppressWarnings("unchecked")
    protected void filterModel(Map<String, Object> model) {
        Object value;
        Map.Entry<String, Object> entry;
        List<String> filterKeys = new ArrayList<String>();

        Iterator<Map.Entry<String, Object>> it = model.entrySet().iterator();
        while(it.hasNext()) {
            entry = (Map.Entry<String, Object>) it.next();
            value = entry.getValue();
            if(value instanceof Map && entry.getKey().endsWith(PROPERTY_EXCLUSIONS)) {
                setPropExclusions((Map<Class, List<String>>)value);
                filterKeys.add(entry.getKey());
            } else if(value instanceof ModelAndView) {
                filterKeys.add(entry.getKey());
            } else if(value instanceof BindingResult) {
                setResult((BindingResult) value);
                filterKeys.add(entry.getKey());
            }
        }

        for(String key : filterKeys) {
            model.remove(key);
        }
    }

    protected abstract void renderErrors();

    protected void writeResponse(HttpServletResponse response, String content) throws IOException {
        response.getWriter().print(content);
    }

    protected void responsePreventCache(HttpServletResponse response) {
        response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
        response.setHeader("Pragma","no-cache"); //HTTP 1.0
        response.setDateHeader("Expires", 0); //prevents caching at the proxy server
    }

    public void setPropExclusions(Map<Class, List<String>> propExclusions) {
        this.propExclusions = propExclusions;
    }

    public Map<Class, List<String>> getPropExclusions() {
        return this.propExclusions;
    }

    public BindingResult getResult() {
        return result;
    }

    public void setResult(BindingResult result) {
        if(this.result != null) {
            for(ObjectError error : result.getAllErrors()) {
                this.result.addError(error);
            }
        } else {
            this.result = result;
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public final void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isPreventCache() {
        return preventCache;
    }

    public final void setPreventCache(boolean preventCache) {
        this.preventCache = preventCache;
    }

    public String getViewName() {
        return this.viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

}
