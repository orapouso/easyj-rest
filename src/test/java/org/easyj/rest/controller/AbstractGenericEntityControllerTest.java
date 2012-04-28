package org.easyj.rest.controller;

import java.util.List;
import org.easyj.orm.SingleService;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.exceptions.ResourceNotFoundException;
import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.domain.TestEntity;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests for non mapping methods
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={ApplicationConfig.class})
public class AbstractGenericEntityControllerTest {

    private AbstractGenericEntityController<TestEntity, Long> controller;
    private TestEntity baseEntity = new TestEntity();
    private MockHttpServletResponse mockResponse = new MockHttpServletResponse();
    private BindingResult mockResult;
        
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
    
    /*delete*/
    @Test(expected=BadRequestException.class)
    public void whenDeleteNullId_throwBadRequest() {
        controller.delete(null);
    }
    
    @Test
    public void whenDeleteValidId_returnDeletedEntity() {
        when(controller.getService().delete(TestEntity.class, 1l)).thenReturn(baseEntity);
        TestEntity deleted = controller.delete(1l);
        assertEquals(baseEntity, deleted);
    }
    
    @Test(expected=ResourceNotFoundException.class)
    public void whenDeleteMissingEntity_throwNotFound() {
        controller.delete(1l);
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
        controller.save(null, mockResult, mockResponse);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveNullResult_throwBadRequest() {
        controller.save(new TestEntity(1l), null, mockResponse);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveNullResponse_throwBadRequest() {
        controller.save(new TestEntity(1l), mockResult, null);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveInvalidEntity_throwBadRequest() {
        when(mockResult.hasErrors()).thenReturn(true);
        
        controller.save(new TestEntity(), mockResult, mockResponse);
    }
    
    @Test(expected=BadRequestException.class)
    public void whenSaveInvalidAndNotValidatedEntity_throwBadRequest() {
        when(controller.getService().save(any())).thenThrow(IllegalArgumentException.class);
        
        controller.save(new TestEntity(), mockResult, mockResponse);
    }
}