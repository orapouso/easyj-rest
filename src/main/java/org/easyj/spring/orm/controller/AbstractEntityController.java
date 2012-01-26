package org.easyj.spring.orm.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.easyj.orm.EntityService;
import org.easyj.orm.jpa.JPAEntityService;
import org.easyj.spring.view.EasyView;
import org.easyj.validation.sequences.POSTSequence;
import org.easyj.validation.sequences.PUTSequence;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractEntityController<T extends Serializable, ID> extends GenericEntityController {

    private Class<T> entityClass;
    
    public <T> ModelAndView post(HttpServletResponse response,
            final T entity, BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(response, entity, result, POSTSequence.class);
    }

    public <T> ModelAndView put(HttpServletResponse response,
            final T entity, BindingResult result) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(response, entity, result, PUTSequence.class);
    }

    private <T> ModelAndView save(HttpServletResponse response, final T entity, BindingResult result, Class validatorSequence) {

        ModelAndView mav = createModelAndView();
        T retEntity = null;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        String retStatus = EntityService.STATUS_ERROR;

        if(entity == null) {
            result.addError(new ObjectError(result.getObjectName(), EntityService.ENTITY_NULL + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot save null entity");
        } else {
            bindValidatorErrors(validator.validate(entity, validatorSequence), result);
            if(!result.hasErrors()) {
                httpResponseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                retStatus = getService().save(entity);
                if(EntityService.STATUS_SUCCESS.equals(retStatus)) {
                    httpResponseStatus = HttpServletResponse.SC_OK;
                    retEntity = (T) getService().loadUK(entity);
                    logger.debug("Entity saved: entity[" + entity + "]");
                } else {
                    if(!EntityService.STATUS_ERROR.equals(retStatus)) {
                        httpResponseStatus = HttpServletResponse.SC_CONFLICT;
                    }
                    result.addError(new ObjectError(result.getObjectName(), retStatus));
                    logger.debug("Entity not saved: return error[" + retStatus + "]");
                }
            }
        }

        configModelAndView(mav, retStatus, retEntity, result);

        response.setStatus(httpResponseStatus);
        return mav;
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public <T> ModelAndView delete(HttpServletResponse response, final ID id) {
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + id);
        ModelAndView mav = createModelAndView();
        BindingResult result = createBindingResult(getEntityClass());
        String retStatus = EntityService.STATUS_ERROR;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;

        if(!checkParam("id", id, result)) {
            result.addError(new ObjectError(result.getObjectName(), EntityService.NO_PARAMS_SET + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot delete entity without params ");
        } else {
            getService().remove(getEntityClass(), new HashMap<String, Object>(){{put("id", id);}});
            retStatus = EntityService.STATUS_SUCCESS;
            httpResponseStatus = HttpServletResponse.SC_OK;
            logger.debug("Entity deleted successfully ");
        }

        mav.addObject(EasyView.STATUS, retStatus);
        mav.addObject(EasyView.BINDING_RESULT, result);
        mav.setViewName(result.getObjectName());

        response.setStatus(httpResponseStatus);
        return mav;
    }

    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ModelAndView get(HttpServletResponse response, T entity, @PathVariable("id") ID id) {
        BindingResult result = createBindingResult(entity.getClass());
        T retEntity = null;

        if(checkParam("id", id, result)) {
            retEntity = (T) getService().load(entity.getClass(), id);
        }

        return get(response, retEntity, result);
    }

    public ModelAndView get(HttpServletResponse response, T entity) {
        return get(response, entity, createBindingResult(entity.getClass()));
    }

    public ModelAndView get(HttpServletResponse response, T entity, BindingResult result) {
        ModelAndView mav = createModelAndView();
        String retStatus = EntityService.STATUS_ERROR;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        if(result != null && !result.hasErrors()) {
            if(entity == null) {
                retStatus = EntityService.ENTITY_NOT_FOUND;
            } else {
                retStatus = EntityService.STATUS_SUCCESS;
            }
            httpResponseStatus = HttpServletResponse.SC_OK;
        }

        configModelAndView(mav, retStatus, entity, result);

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
    
    protected Class<T> getEntityClass() {
        
        if(entityClass == null) {
            ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
            entityClass = (Class<T>) pt.getActualTypeArguments()[0];
        }
        
        return entityClass;
        
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