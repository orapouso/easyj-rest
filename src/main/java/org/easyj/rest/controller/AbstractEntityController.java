package org.easyj.rest.controller;

import java.util.Map;
import org.easyj.orm.EntityService;
import org.easyj.orm.SingleService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public abstract class AbstractEntityController extends AbstractController {

    protected SingleService service;

    public abstract void setService(SingleService service);

    public abstract SingleService getService();

    protected void checkParams(Map<String, Object> params, BindingResult result) {
        Object param;
        for(String key : params.keySet()) {
            param = params.get(key);
            checkParam(key, param, result);
        }
    }

    protected boolean checkParam(String key, Object param, BindingResult result) {
        if(param == null) {
            result.addError(new FieldError(result.getObjectName(), key, null, false, null, null, EntityService.NULL_PARAM));
            return false;
        }
        if(param instanceof Number && ((Number) param).intValue() < 0) {
            result.addError(new FieldError(result.getObjectName(), key, param, false, null, null, EntityService.INVALID_PARAM));
            return false;
        }
        if(param instanceof String && "".equals(((String) param).trim())) {
            result.addError(new FieldError(result.getObjectName(), key, param, false, null, null, EntityService.INVALID_PARAM));
            return false;
        }
        return true;
    }
}
