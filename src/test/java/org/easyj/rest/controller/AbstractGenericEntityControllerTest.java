package org.easyj.rest.controller;

import org.easyj.orm.SingleService;
import org.easyj.rest.exceptions.BadRequestException;
import org.easyj.rest.test.domain.TestEntity;
import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AbstractGenericEntityControllerTest {

    private AbstractGenericEntityController<TestEntity, Long> controller;
    private TestEntity baseEntity = new TestEntity();
        
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
        
        controller.setService(null);
    }
    
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
    
    @Test
    public void whenGetEntityClass_returnEntityClass() {
        assertEquals(TestEntity.class, controller.getEntityClass());
    }
    
}