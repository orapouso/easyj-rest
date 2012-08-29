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

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.easyj.rest.view.jsonlib.DateFormatJsonValueProcessor;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Specific JSON View
 * <br><br>
 * Constructs error structure and ensures correct property exclusions
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 */
public class JSONView extends EasyView {

    private JSONObject json;
    private String dateFormat;
    
    public JSONView() {
        super();
        setContentType("application/json");
        if(dateFormat == null) setDateFormat(DateFormatJsonValueProcessor.DEFAULT_DATE_FORMAT);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        setUp(response, model);

        modelToJSON(model);

        writeResponse(response, json.toString());
    }

    @Override
    protected void setUp(HttpServletResponse response, Map<String, Object> model) {
        json = new JSONObject();
        super.setUp(response, model);
    }

    @Override
    protected void renderErrors() {
        if(getResult() != null && getResult().hasErrors()) {
            json.accumulate(EasyView.ERRORS, getJSONErrors(getResult()));
        }
    }

    private void modelToJSON(Map<String, Object> model) {
        JsonConfig jsonConfig = setJSONConfig();
        Object o;
        for(String key : model.keySet()) {
            o = model.get(key);

            if(o != null) {
                json.element(key, o, jsonConfig);
            }
        }
    }

    private JsonConfig setJSONConfig() {
        Map<Class, List<String>> exclusions = getPropExclusions();
        JsonConfig config = new JsonConfig();
        if(exclusions != null && !exclusions.isEmpty()) {
            for(Class klass : exclusions.keySet()) {
                config.registerPropertyExclusions(klass, StringUtils.toStringArray(exclusions.get(klass)));
            }
            config.registerJsonValueProcessor(Date.class, new DateFormatJsonValueProcessor(dateFormat));
        }
        return config;
    }

    public <T> JSONObject getJSONErrors(BindingResult result) {
        JSONObject errors = new JSONObject();
        JSONObject jsonError;
        String errorName;
        if(result != null) {
            for(ObjectError error : result.getAllErrors()) {
                errorName = error.getObjectName();
                jsonError = new JSONObject()
                        .element("message", error.getDefaultMessage());
                if(error instanceof FieldError) {
                    jsonError.element("rejectedValue", ((FieldError) error).getRejectedValue());
                    errorName = ((FieldError) error).getField();
                }

                errors.element(errorName, jsonError);
            }
        }
        return errors;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public final void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}
