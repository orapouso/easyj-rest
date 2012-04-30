package org.easyj.rest.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.exceptions.ConflictException;
import org.easyj.rest.exceptions.ResourceNotFoundException;
import org.easyj.rest.validation.sequences.POSTSequence;
import org.easyj.rest.validation.sequences.PUTSequence;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractGenericEntityController<E extends Serializable, ID> extends AbstractEntityController implements EntityController<E, ID> {

    private Class<E> entityClass;
    
    @Override
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView post(@ModelAttribute @Validated(POSTSequence.class) E entity, BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result);
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ModelAndView put(@ModelAttribute @Validated(PUTSequence.class) E entity, BindingResult result) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);
        
        return save(entity, result);
    }
    
    protected ModelAndView save(E entity, BindingResult result) {
        if(entity == null || result == null) {
            logger.debug("ERROR: Cannot save: some parameter is null entity[{}], result[{}]", 
                new Object[]{entity, result}
            );
            throw new BadRequestException();
        } else {
            if(result.hasErrors()) {
                logger.debug("ERROR: Cannot save: missing or wrong parameters: ERRORS FOUND[{}]", result.getErrorCount());
                throw new BadRequestException(result);
            } else {
                logger.debug("Entity SAVING: entity[" + entity + "]");
                E retEntity = persist(entity);
                logger.debug("Entity SAVED: entity[" + entity + "]");

                return configMAV(retEntity, result);
            }
        }

    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ModelAndView delete(ID primaryKey) {
        BindingResult result = createBindingResult(getEntityClass());
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        E entity = null;

        if(!checkParam("id", primaryKey, result)) {
            logger.error("ERROR: Cannot delete entity without params ");
            throw new BadRequestException();
        } else {
            entity = remove(primaryKey);
            logger.debug("Entity deleted successfully ");
        }

        return configMAV(entity, result);
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ModelAndView get(@PathVariable("id") ID id) {
        BindingResult result = createBindingResult(getEntityClass());
        
        E entity = null;

        if(checkParam("id", id, result)) {
            entity = findOne(id);
        }
        
        return get(entity, result);
    }
    
    public ModelAndView get(E entity, BindingResult result) {
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
    public ModelAndView getAll() {
        return configMAV(findAll());
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

    protected E remove(ID id) {
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