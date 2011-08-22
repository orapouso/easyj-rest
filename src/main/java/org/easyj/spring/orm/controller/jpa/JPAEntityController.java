package org.easyj.spring.orm.controller.jpa;

import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.easyj.orm.EntityService;
import org.easyj.orm.jpa.JPAEntityService;
import org.easyj.spring.orm.controller.GenericEntityController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class JPAEntityController extends GenericEntityController {

    public <T> ModelAndView get(HttpServletResponse response, Class<T> klazz, Object id) {
        BindingResult result = createBindingResult(klazz);
        T retEntity = null;

        if(checkParam("id", id, result)) {
            retEntity = (T) getService().load(klazz, id);
        }

        return get(response, retEntity, result);
    }

    public <T> ModelAndView get(HttpServletResponse response, Object data) {
        return get(response, data, createBindingResult(data.getClass()));
    }

    public <T> ModelAndView get(HttpServletResponse response, Object data, BindingResult result) {
        ModelAndView mav = createModelAndView();
        String retStatus = EntityService.STATUS_ERROR;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        if(result != null && !result.hasErrors()) {
            if(data == null) {
                retStatus = EntityService.ENTITY_NOT_FOUND;
            } else {
                retStatus = EntityService.STATUS_SUCCESS;
            }
            httpResponseStatus = HttpServletResponse.SC_OK;
        }

        configModelAndView(mav, retStatus, data, result);

        response.setStatus(httpResponseStatus);
        return mav;
    }

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

    @Resource(name="JPAEntityService")
    @Override
    public void setService(EntityService service) {
        this.service = (JPAEntityService) service;
    }

    @Override
    public JPAEntityService getService() {
        return (JPAEntityService) this.service;
    }

}
