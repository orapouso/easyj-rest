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

package org.easyj.rest.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.validation.groups.Default;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.exceptions.ConflictException;
import org.easyj.rest.exceptions.ResourceNotFoundException;
import org.easyj.rest.validation.groups.POSTChecks;
import org.easyj.rest.validation.groups.PUTChecks;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generic controller class to be implemented with concrete {@code @Entities}
 * 
 * This class should provide basic CRUD funcionality with little or no
 * configuration needed
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
public abstract class AbstractGenericEntityController<E extends Serializable, ID> extends AbstractEntityController implements EntityController<E, ID> {

    private Class<E> entityClass;

    @RequestMapping(value={"/create", "/edit", "/new"}, produces={MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE, "application/html+xml"})
    public ModelAndView create() throws InstantiationException, IllegalAccessException {
        ModelAndView mav = configMAV(getEntityClass().newInstance(), getCreateViewName());
        
        return modelToForm(mav);
    }
    
    @RequestMapping(value="/{id}/edit", produces={MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE, "application/html+xml"})
    public ModelAndView edit(@PathVariable("id") ID primaryKey) throws InstantiationException, IllegalAccessException {
        ModelAndView mav;

        Object data = null;
        if(primaryKey != null) {
            data = retrieve(primaryKey);
        }
        
        mav = configMAV(data, getEditViewName());
        
        return modelToForm(mav);
    }
    
    @Override
    @RequestMapping(method=RequestMethod.POST)
    public ModelAndView post(@ModelAttribute("data") @Validated(value={Default.class, POSTChecks.class}) E entity, BindingResult result) {
        logger.debug("Receiving POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result, null);
    }

    @RequestMapping(method=RequestMethod.POST, produces={MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE, "application/html+xml"})
    public ModelAndView postHTML(@ModelAttribute("data") @Validated(value={Default.class, POSTChecks.class}) E entity, BindingResult result) {
        logger.debug("Receiving HTML POST Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result, getPostViewName());
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public ModelAndView put(@ModelAttribute("data") @Validated(value={Default.class, PUTChecks.class}) E entity, BindingResult result, @PathVariable("id") ID id) {
        logger.debug("Receiving PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result, null);
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.PUT, produces={MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE, "application/html+xml"})
    public ModelAndView putHTML(@ModelAttribute("data") @Validated(value={Default.class, PUTChecks.class}) E entity, BindingResult result, @PathVariable("id") ID id) {
        logger.debug("Receiving HTML PUT Request for: " + entity.getClass().getSimpleName() + ": " + entity);

        return save(entity, result, getPutViewName().replace("{id}", id.toString()));
    }
    
    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public ModelAndView delete(@PathVariable("id") ID primaryKey) {
        logger.debug("Receiving DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        
        E entity = remove(primaryKey);
        
        return configMAV(entity);
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE, produces={MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE, "application/html+xml"})
    public ModelAndView deleteHTML(@PathVariable("id") ID primaryKey) {
        logger.debug("Receiving HTML DELETE Request for: " + getEntityClass().getSimpleName() + ": " + primaryKey);
        
        E entity = remove(primaryKey);
        
        return configMAV(entity, getDeleteViewName());
    }

    @Override
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public ModelAndView get(@PathVariable("id") ID primaryKey) {
        E entity = retrieve(primaryKey);
        
        ModelAndView mav = configMAV(entity, getGetViewName());

        return mav;        
    }
    
    @Override
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getAll() {
        return configMAV(findAll(), getListViewName());
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
    
    protected ModelAndView save(E entity, BindingResult result, String viewName) {
        if(entity == null || result == null) {
            logger.debug("ERROR: Cannot save: some parameter is null entity[{}], result[{}]", 
                new Object[]{entity, result}
            );
            throw new BadRequestException(configMAV(entity, result, getEditViewName()));
        } else {
            for(Validator val : getValidators()) {
                val.validate(entity, result);
            }
            
            if(result.hasErrors()) {
                logger.debug("ERROR: Cannot save: missing or wrong parameters: ERRORS FOUND[{}]", result.getErrorCount());
                throw new BadRequestException(configMAV(entity, result, getEditViewName()));
            } else {
                logger.debug("Entity SAVING: entity[" + entity + "]");
                E retEntity = persist(entity);
                logger.debug("Entity SAVED: entity[" + entity + "]");

                return configMAV(retEntity, result, viewName);
            }
        }

    }
    
    protected E persist(E entity) {
        ModelAndView mav = configMAV(entity, getEditViewName());
        if(entity == null) {
            throw new BadRequestException("Cannot persist null entity", mav);
        }
        
        try {
            return getService().save(entity);
        } catch(IllegalStateException ex){
            logger.error("Entity not found on persist operation for: [{}]", entity, ex);
            throw new ResourceNotFoundException(ex);
        } catch(IllegalArgumentException ex) {
            logger.error("IllegalArgumentException on persist operation for: [{}]", entity, ex);
            throw new BadRequestException(ex, mav);
        } catch(DataIntegrityViolationException ex){ // on unique constraint
            logger.error("DataIntegrityViolationException on persist operation for: [{}]", entity, ex);
            throw new ConflictException(ex, mav);
        }
    }

    protected E remove(ID id) {
        E deleted;
        if(id == null) {
            throw new BadRequestException("Cannot delete null id");
        }
        
        deleted = getService().delete(getEntityClass(), id);
        
        if(deleted == null) {
            throw new ResourceNotFoundException("Resource to be deleted doesn't exists");
        }

        logger.debug("Entity deleted successfully");
        
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