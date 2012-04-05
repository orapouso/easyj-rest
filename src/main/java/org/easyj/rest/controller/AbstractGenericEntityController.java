package org.easyj.rest.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.easyj.orm.EntityService;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.exceptions.ConflictException;
import org.easyj.rest.exceptions.ResourceNotFoundException;
import org.easyj.rest.validation.sequences.POSTSequence;
import org.easyj.rest.validation.sequences.PUTSequence;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractGenericEntityController<E extends Serializable, ID> extends AbstractEntityController implements EntityController<E, ID> {

    private Class<E> entityClass;
    
    @Override
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView post(@ModelAttribute E entity, HttpServletResponse response, BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, response, result, POSTSequence.class);
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ModelAndView put(@ModelAttribute E entity, HttpServletResponse response, BindingResult result) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);
        
        return save(entity, response, result, PUTSequence.class);
    }
    
    protected ModelAndView save(E entity, HttpServletResponse response, BindingResult result, Class validatorSequence) {
        E retEntity = null;

        if(entity == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.addError(new ObjectError(result.getObjectName(), EntityService.ENTITY_NULL + "." + result.getTarget().getClass().getSimpleName()));
            logger.debug("ERROR: Cannot save null entity");
        } else {
            bindValidatorErrors(validator.validate(entity, validatorSequence), result);
            if(!result.hasErrors()) {
                retEntity = persist(entity);
                logger.debug("Entity saved: entity[" + entity + "]");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        return configMAV(retEntity, result);
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ModelAndView delete(ID primaryKey, HttpServletResponse response) {
        BindingResult result = createBindingResult(getEntityClass());
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        E entity = null;

        if(!checkParam("id", primaryKey, result)) {
            logger.error("ERROR: Cannot delete entity without params ");
            throw new BadRequestException();
        } else {
            entity = delete(primaryKey);
            logger.debug("Entity deleted successfully ");
        }

        return configMAV(entity, result);
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
        if(result != null && result.hasErrors()) {
            throw new BadRequestException();
        } else if(entity == null) {
            throw new ResourceNotFoundException();
        }

        ModelAndView mav = configMAV(entity, result);

        return mav;        
    }
    
    @Override
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getAll(HttpServletResponse response) {
        return configMAV(findAll());
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
        if(entity == null) {
            throw new BadRequestException("Cannot persist null entity");
        }
        
        try {
            return getService().save(entity);
        } catch(IllegalStateException ex){
            logger.error("Entity not found on update operation for: [{}]", entity, ex);
            throw new ResourceNotFoundException(ex);
        } catch(IllegalArgumentException ex) {
            logger.error("IllegalArgumentException on create operation for: [{}]", entity, ex);
            throw new BadRequestException(ex);
        } catch(DataIntegrityViolationException ex){ // on unique constraint
            logger.error("DataIntegrityViolationException on create operation for: [{}]", entity, ex);
            throw new ConflictException(ex);
        }
    }

    protected E delete(ID id) {
        E deleted;
        if(id == null) {
            throw new BadRequestException("Cannot delete null id");
        }
        
        deleted = getService().delete(getEntityClass(), id);
        
        if(deleted == null) {
            throw new ResourceNotFoundException("Deleted resource don't ");
        }

        return deleted;
    }

    protected E findOne(ID primaryKey) {
        E entity = null;
        
        if(primaryKey == null) {
            throw new BadRequestException();
        }
        
        try {
            entity = getService().findOne(getEntityClass(), primaryKey);
        } catch(Exception ex) {
            logger.error("", ex);
            throw new BadRequestException();
        }
        
        if(entity == null) {
            throw new ResourceNotFoundException();
        }
        
        return entity;
    }

    protected List<E> findAll() {
        try {
            return getService().findAll(getEntityClass());
        } catch (Exception ex) {
            throw new BadRequestException();
        }
   }

}