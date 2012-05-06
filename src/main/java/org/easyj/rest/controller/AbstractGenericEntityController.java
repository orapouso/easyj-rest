package org.easyj.rest.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value={"/create", "/{id}/edit"})
    public ModelAndView form(@PathVariable("id") ID primaryKey) {
        ModelAndView mav;

        Object data = null;
        if(primaryKey != null) {
            data = retrieve(primaryKey);
        }
        
        mav = configMAV(data, formViewName);
        
        return modelToForm(mav);
    }
    
    @Override
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView post(@ModelAttribute @Validated(POSTSequence.class) E entity, BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result);
    }

    @RequestMapping(method=RequestMethod.POST, headers={"accept=text/html,application/html+xml"})
    public ModelAndView postHTML(@ModelAttribute @Validated(POSTSequence.class) E entity, BindingResult result, HttpServletRequest request) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result, "redirect:" + request.getRequestURI());
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ModelAndView put(@ModelAttribute @Validated(PUTSequence.class) E entity, BindingResult result) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.PUT, headers={"accept=text/html,application/html+xml"})
    public ModelAndView putHTML(@ModelAttribute @Validated(PUTSequence.class) E entity, BindingResult result, @PathVariable("id") ID id, HttpServletRequest request) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result, "redirect:" + request.getRequestURI());
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ModelAndView delete(@PathVariable("id") ID primaryKey) {
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        
        E entity = remove(primaryKey);
        logger.debug("Entity deleted successfully");
        
        return configMAV(entity);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE, headers={"accept=text/html,application/html+xml"})
    public ModelAndView deleteHTML(@PathVariable("id") ID primaryKey) {
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        
        E entity = remove(primaryKey);
        logger.debug("Entity deleted successfully");
        
        return configMAV(entity, entityViewName);
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ModelAndView get(@PathVariable("id") ID primaryKey) {
        E entity = retrieve(primaryKey);
        
        ModelAndView mav = configMAV(entity);

        return mav;        
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.GET, headers={"accept=text/html,application/html+xml"})
    public ModelAndView getHTML(@PathVariable("id") ID primaryKey) {
        E entity = retrieve(primaryKey);
        
        ModelAndView mav = configMAV(entity, entityViewName);

        return mav;        
    }
    
    @Override
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getAll() {
        return configMAV(findAll());
    }
    
    @RequestMapping(method=RequestMethod.GET, headers={"accept=text/html,application/html+xml"})
    public ModelAndView getAllHTML() {
        return configMAV(findAll(), listViewName);
    }
    
    protected Class<E> getEntityClass() {
        
        if(entityClass == null) {
            ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
            entityClass = (Class<E>) pt.getActualTypeArguments()[0];
        }
        
        return entityClass;
        
    }

    protected E retrieve(ID primaryKey) {
        E entity = findOne(primaryKey);
        
        if(entity == null) {
            throw new ResourceNotFoundException();
        }
        
        return entity;
    }
    
    protected ModelAndView save(E entity, BindingResult result) {
        return save(entity, result, null);
    }
    
    protected ModelAndView save(E entity, BindingResult result, String viewName) {
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

                return configMAV(retEntity, result, viewName);
            }
        }

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
            throw new ResourceNotFoundException("Deleted resource don't");
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
    
    protected ModelAndView modelToForm(ModelAndView mav) {
        return mav;
    }
    
}