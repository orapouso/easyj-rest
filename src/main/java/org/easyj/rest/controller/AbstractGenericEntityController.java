package org.easyj.rest.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.easyj.orm.EntityService;
import org.easyj.validation.sequences.POSTSequence;
import org.easyj.validation.sequences.PUTSequence;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*TODO
 * abstract persistence layer methods so, each persistence can implement its methods with apropriate service
 * better response handling using pre-defined methods with HttpStatus
 * create all correspondent RequestMappings
 */
public abstract class AbstractGenericEntityController<E extends Serializable, ID> extends GenericEntityController implements EntityController<E, ID> {

    private Class<E> entityClass;
    
    @Override
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView post(@ModelAttribute E entity, HttpServletResponse response,
            BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, response, result, POSTSequence.class);
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ModelAndView put(@ModelAttribute E entity, HttpServletResponse response, BindingResult result) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);
        
        return save(entity, response, result, PUTSequence.class);
    }
    
    private ModelAndView save(E entity, HttpServletResponse response, BindingResult result, Class validatorSequence) {
        ModelAndView mav = new ModelAndView();
        E retEntity = null;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        String retStatus = EntityService.STATUS_ERROR;

        if(entity == null) {
            result.addError(new ObjectError(result.getObjectName(), EntityService.ENTITY_NULL + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot save null entity");
        } else {
            bindValidatorErrors(validator.validate(entity, validatorSequence), result);
            if(!result.hasErrors()) {
                httpResponseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                retEntity = persist(entity);
                if(retEntity != null) {
                    httpResponseStatus = HttpServletResponse.SC_OK;
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

        configMAV(mav, retStatus, retEntity, result);

        response.setStatus(httpResponseStatus);
        return mav;
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ModelAndView delete(ID primaryKey, HttpServletResponse response) {
        BindingResult result = createBindingResult(getEntityClass());
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        ModelAndView mav = new ModelAndView();
        String retStatus = EntityService.STATUS_ERROR;
        int httpResponseStatus = HttpServletResponse.SC_BAD_REQUEST;
        E entity = null;

        if(!checkParam("id", primaryKey, result)) {
            result.addError(new ObjectError(result.getObjectName(), EntityService.NO_PARAMS_SET + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot delete entity without params ");
        } else {
            entity = delete(primaryKey);
            retStatus = EntityService.STATUS_SUCCESS;
            httpResponseStatus = HttpServletResponse.SC_OK;
            logger.debug("Entity deleted successfully ");
        }

        configMAV(mav, retStatus, entity, result);
        response.setStatus(httpResponseStatus);
        return mav;
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ModelAndView get(@PathVariable("id") ID id, HttpServletResponse response) {
        BindingResult result = createBindingResult(getEntityClass());
        
        E entity = null;

        if(checkParam("id", id, result)) {
            entity = findOne(id);
        }
        
        return get(entity, response, result);
    }
    
    public ModelAndView get(E entity, HttpServletResponse response, BindingResult result) {
        ModelAndView mav = new ModelAndView();
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

        configMAV(mav, retStatus, entity, result);

        response.setStatus(httpResponseStatus);
        
        return mav;        
    }
    
    @Override
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getAll(HttpServletResponse response) {
        return getAll(findAll(), response);
    }
    
    public ModelAndView getAll(List<E> entities, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String retStatus = EntityService.STATUS_ERROR;
        int httpResponseStatus = HttpServletResponse.SC_OK;
        
        configMAV(mav, retStatus, entities, "getAll");
        
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
    
    protected Class<E> getEntityClass() {
        
        if(entityClass == null) {
            ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
            entityClass = (Class<E>) pt.getActualTypeArguments()[0];
        }
        
        return entityClass;
        
    }

    protected E persist(E entity) {
        return getService().save(entity);
    }

    protected E delete(ID id) {
        return getService().delete(getEntityClass(), id);
    }

    protected E findOne(ID primaryKey) {
        return getService().findOne(getEntityClass(), primaryKey);
    }

    protected List<E> findAll() {
        return getService().findAll(getEntityClass());
    }

}