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

import java.util.Date;
import java.util.List;
import org.easyj.orm.SingleService;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.exceptions.ConflictException;
import org.easyj.rest.exceptions.ResourceNotFoundException;
import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.domain.TestEntity;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.ModelAndViewAssert.*;

/**
 * Tests for non mapping methods
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={ApplicationConfig.class})
public class AbstractGenericEntityControllerTest {

    private AbstractGenericEntityController<TestEntity, Long> controller;
    private TestEntity baseEntity = new TestEntity();
    private BindingResult mockResult;
    private String stubViewName = "stubViewName";
     
    @Before
    public void before() {
        controller = new AbstractGenericEntityController<TestEntity, Long>() {

            @Override
            public void setService(SingleService service) {
                this.service = mock(SingleService.class);
            }

            @Override
            public SingleService getService() {
                return service;
            }
        };
        
        controller.initialize();
        
        mockResult = mock(BindingResult.class);
        
        controller.setService(null);
    }
    
    /*getEntityClass*/
    @Test
    public void whenGetEntityClass_returnEntityClass() {
        assertEquals(TestEntity.class, controller.getEntityClass());
    }
    
    /*persist*/
    @Test(expected=BadRequestException.class)
    public void whenPersistNullEntity_throwBadRequest() {
        when(controller.getService().save(any())).thenReturn(baseEntity);
        
        controller.persist(null);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenPersistEmptyEntity_throwBadRequest() {
        when(controller.getService().save(baseEntity)).thenThrow(IllegalArgumentException.class);
        
        controller.persist(baseEntity);
    }
    
    /*remove*/
    @Test(expected=BadRequestException.class)
    public void whenRemoveNullId_throwBadRequest() {
        controller.remove(null);
    }
    
    @Test
    public void whenRemoveValidId_returnDeletedEntity() {
        when(controller.getService().delete(TestEntity.class, 1l)).thenReturn(baseEntity);
        TestEntity removed = controller.remove(1l);
        assertEquals(baseEntity, removed);
    }
    
    @Test(expected=ResourceNotFoundException.class)
    public void whenRemoveMissingEntity_throwNotFound() {
        controller.remove(1l);
    }
    
    /*findOne*/
    @Test(expected=BadRequestException.class)
    public void whenFindOneNullId_throwBadRequest() {
        controller.findOne(null);
    }
    
    /*findAll*/
    @Test
    public void whenFindall_returnNotNullList() {
        List<TestEntity> list = controller.findAll();
        assertNotNull(list);
    }
    
    /*save*/
    @Test(expected=BadRequestException.class)
    public void whenSaveNullEntity_throwBadRequest() {
        controller.save(null, mockResult, stubViewName);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveNullResult_throwBadRequest() {
        controller.save(new TestEntity(1l), null, stubViewName);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveInvalidEntity_throwBadRequest() {
        when(mockResult.hasErrors()).thenReturn(true);
        
        controller.save(new TestEntity(), mockResult, stubViewName);
    }
    
    @Test(expected=ConflictException.class)
    public void whenSaveNewExistingEntity_throwConflict() {
        when(controller.getService().save(any())).thenThrow(DataIntegrityViolationException.class);
        
        controller.save(new TestEntity(1l), mockResult, stubViewName);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveInvalidAndNotValidatedEntity_throwBadRequest() {
        when(controller.getService().save(any())).thenThrow(IllegalArgumentException.class);
        
        controller.save(new TestEntity(), mockResult, stubViewName);
    }

    @Test
    public void whenSaveValidEntity_returnModelAndView() {
        TestEntity entity = new TestEntity();
        entity.setFirstName("firtsName");
        entity.setLastName("lastName");
        entity.setTestDate(new Date());
        
        TestEntity returnEntity = new TestEntity(1l);
        entity.setFirstName("firtsName");
        entity.setLastName("lastName");
        entity.setTestDate(new Date());
        
        when(controller.getService().save(any())).thenReturn(returnEntity);
        
        ModelAndView mav = controller.save(entity, mockResult, stubViewName);
        
        TestEntity saved = assertAndReturnModelAttributeOfType(mav, "data", TestEntity.class);
        assertEquals(returnEntity, saved);
        
    }
}