package org.easyj.rest.test.controller;

import java.util.ArrayList;
import java.util.Date;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.config.PersistenceJPAConfig;
import org.easyj.rest.test.config.WebConfig;
import org.easyj.rest.test.domain.TestEntity;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.ModelAndViewAssert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={ApplicationConfig.class, PersistenceJPAConfig.class})
public class TestEntityControllerTest {

    @Autowired
    private ApplicationContext ac;
    
    private SingleJPAEntityService singleJPAEntityService;
    
    private static MockMvc mvc;
    
    private TestEntity baseEntity = new TestEntity();
    private TestEntity postEntity = new TestEntity();
    private TestEntity putEntity = new TestEntity();
    
    private String firstName = "firstName";
    private String lastName = "lastName";
    private String testDateKey = "testDate";
    private String testDate = "20/12/1980";
        
    @Before
    public void before() {
        mvc = annotationConfigSetup(WebConfig.class).
                setParentContext(ac).build();
        
        singleJPAEntityService = ac.getBean(SingleJPAEntityService.class);
        reset(singleJPAEntityService);
        
        baseEntity.setId(1l);
        baseEntity.setFirstName(firstName);
        baseEntity.setLastName(lastName);
        baseEntity.setTestDate(new Date());
        
        postEntity.setFirstName(baseEntity.getFirstName());
        postEntity.setLastName(baseEntity.getLastName());
        postEntity.setTestDate(baseEntity.getTestDate());
        
        putEntity.setId(baseEntity.getId());
    }
    
    /*GET*/
    @Test
    public void whenGETEntityWithNoId_returnAllEntities() throws Exception {
        when(singleJPAEntityService.findAll(TestEntity.class))
                .thenReturn(new ArrayList<TestEntity>())
                .thenReturn(new ArrayList<TestEntity>(){{add(baseEntity); add(new TestEntity(2l));}});
        
        mvc.perform(get("/entity").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", empty()))
           .andExpect(model().attribute("result", nullValue()));

        mvc.perform(get("/entity").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", not(empty())))
           .andExpect(model().attribute("data", hasSize(2)))
           .andExpect(model().attribute("result", nullValue()));
    }
    
    @Test
    public void whenGETMissingEntity_return404() throws Exception {
        mvc.perform(get("/entity/15").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    public void whenGETBindError_returnBadRequest() throws Exception {
        when(singleJPAEntityService.findOne(TestEntity.class, 1l)).thenReturn(baseEntity);
        
        MvcResult result = mvc.perform(get("/entity/a").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andReturn();

        BindingResult bindingResult = assertAndReturnModelAttributeOfType(result.getModelAndView(), "result", BindingResult.class);
        
        //Validation errors should be bound to result as FieldError
        assertEquals(false, bindingResult.hasGlobalErrors());
        assertEquals(true, bindingResult.hasFieldErrors());
        assertEquals(1, bindingResult.getFieldErrorCount());
    }

    @Test
    public void whenGETExistingEntity_returnEntity() throws Exception {
        when(singleJPAEntityService.findOne(TestEntity.class, 1l)).thenReturn(baseEntity);
        
        MvcResult result = mvc.perform(get("/entity/1").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", notNullValue()))
           .andExpect(model().attribute("result", nullValue()))
           .andReturn();

        TestEntity returnedEntity = assertAndReturnModelAttributeOfType(result.getModelAndView(), "data", TestEntity.class);
        
        assertThat(returnedEntity, equalTo(baseEntity));
    }

    /*POST*/
    @Test
    public void whenPOSTWrongURL_returnMethodNotAllowed() throws Exception {
        mvc.perform(
                post("/entity/1")
            )
           .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void whenPOSTNewEntity_returnEntityWithId() throws Exception {
        when(singleJPAEntityService.save(postEntity)).thenReturn(baseEntity);
        
        MvcResult result = mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               .param(lastName, lastName)
               .param(testDateKey, testDate)
            )
           .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntity.class)))
           .andReturn();
        
        TestEntity returnedEntity = assertAndReturnModelAttributeOfType(result.getModelAndView(), "data", TestEntity.class);
        
        assertThat(returnedEntity, equalTo(baseEntity));
    }

    @Test
    public void whenPOSTExistingEntity_returnConflict() throws Exception {
        when(singleJPAEntityService.save(baseEntity)).thenThrow(DataIntegrityViolationException.class);
        
        mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               .param(lastName, lastName)
               .param(testDateKey, testDate)
            )
           .andExpect(status().isConflict());
    }

    @Test
    public void whenPOSTWithWrongParams_returnBadRequest() throws Exception {
        BindingResult bindingResult;
        MvcResult result;
        
        result = mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               .param("id", "1")//@Id should be null on POSTs
               .param(firstName, firstName)
               .param(lastName, lastName)
               .param(testDateKey, testDate)
            )
           .andExpect(status().isBadRequest())
           .andExpect(model().attribute("result", not(nullValue())))
           .andExpect(model().attribute("data", nullValue()))
           .andReturn();

        bindingResult = assertAndReturnModelAttributeOfType(result.getModelAndView(), "result", BindingResult.class);
        
        //Validation errors should be bound to result as FieldError
        assertEquals(false, bindingResult.hasGlobalErrors());
        assertEquals(true, bindingResult.hasFieldErrors());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertThat(bindingResult.getTarget(), instanceOf(TestEntity.class));
        assertThat(bindingResult.getFieldError("id"), notNullValue());

        result = mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               //Not posting firstName a @NotNull param
               .param(lastName, lastName)
               .param(testDateKey, testDate)
            )
           .andExpect(status().isBadRequest())
           .andExpect(model().attribute("result", not(nullValue())))
           .andExpect(model().attribute("data", nullValue()))
           .andReturn();

        bindingResult = assertAndReturnModelAttributeOfType(result.getModelAndView(), "result", BindingResult.class);
        
        //Validation errors should be bound to result as FieldError
        assertEquals(false, bindingResult.hasGlobalErrors());
        assertEquals(true, bindingResult.hasFieldErrors());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertThat(bindingResult.getTarget(), instanceOf(TestEntity.class));
        //Missing params should be binded to its own field name
        assertThat(bindingResult.getFieldError(firstName), notNullValue());

        result = mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               //Not posting lastName a different @NotNull param
               .param(testDateKey, testDate)
            )
           .andExpect(status().isBadRequest())
           .andExpect(model().attribute("result", not(nullValue())))
           .andExpect(model().attribute("data", nullValue()))
           .andReturn();
        
        bindingResult = assertAndReturnModelAttributeOfType(result.getModelAndView(), "result", BindingResult.class);

        //Validation errors should be bound to result as FieldError
        assertEquals(false, bindingResult.hasGlobalErrors());
        assertEquals(true, bindingResult.hasFieldErrors());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertThat(bindingResult.getTarget(), instanceOf(TestEntity.class));
        //Missing params should be binded to its own field name
        assertThat(bindingResult.getFieldError(lastName), notNullValue());
    }
    
    @Test
    public void whenPOSTWithMissingNullParam_returnEntityWithId() throws Exception {
        when(singleJPAEntityService.save(postEntity)).thenReturn(baseEntity);

        MvcResult result = mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               .param(lastName, lastName)
               //not posting testDate non @NotNull
            )
            .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntity.class)))
           .andReturn();
        
        TestEntity returnedEntity = assertAndReturnModelAttributeOfType(result.getModelAndView(), "data", TestEntity.class);
        
        assertThat(returnedEntity, equalTo(baseEntity));
   }

    /*PUT*/
    @Test
    public void whenPUTNewEntity_returnMethodNotAllowed() throws Exception {
        mvc.perform(
                put("/entity")
            )
           .andExpect(status().isMethodNotAllowed());

        mvc.perform(
                put("/entity")
               .param("id", "1")//even giving the id as a param, mapping does not exists and should abort
            )
           .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void whenPUTEntity_returnEntity() throws Exception {
        when(singleJPAEntityService.save(putEntity)).thenReturn(putEntity);
        
        MvcResult result = mvc.perform(
                put("/entity/1")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               .param(lastName, lastName)
               .param(testDateKey, testDate)
            )
           .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntity.class)))
           .andReturn();
        
        TestEntity returnedEntity = assertAndReturnModelAttributeOfType(result.getModelAndView(), "data", TestEntity.class);
        
        assertThat(returnedEntity, equalTo(baseEntity));
    }
    
    @Test
    public void whenPUTWithWrongParams_returnBadRequest() throws Exception {
        BindingResult bindingResult;
        MvcResult result;
        
        result = mvc.perform(
                put("/entity/1")
               .accept(MediaType.APPLICATION_JSON)
               //Not posting firstName a @NotNull param
               .param(lastName, lastName)
               .param(testDateKey, testDate)
            )
           .andExpect(status().isBadRequest())
           .andExpect(model().attribute("result", not(nullValue())))
           .andExpect(model().attribute("data", nullValue()))
           .andReturn();

        bindingResult = assertAndReturnModelAttributeOfType(result.getModelAndView(), "result", BindingResult.class);
        
        //Validation errors should be bound to result as FieldError
        assertEquals(false, bindingResult.hasGlobalErrors());
        assertEquals(true, bindingResult.hasFieldErrors());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertThat(bindingResult.getTarget(), instanceOf(TestEntity.class));
        //Missing params should be binded to its own field name
        assertThat(bindingResult.getFieldError(firstName), notNullValue());

        result = mvc.perform(
                put("/entity/1")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               //Not posting lastName a different @NotNull param
               .param(testDateKey, testDate)
            )
           .andExpect(status().isBadRequest())
           .andExpect(model().attribute("result", not(nullValue())))
           .andExpect(model().attribute("data", nullValue()))
           .andReturn();
        
        bindingResult = assertAndReturnModelAttributeOfType(result.getModelAndView(), "result", BindingResult.class);

        //Validation errors should be bound to result as FieldError
        assertEquals(false, bindingResult.hasGlobalErrors());
        assertEquals(true, bindingResult.hasFieldErrors());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertThat(bindingResult.getTarget(), instanceOf(TestEntity.class));
        //Missing params should be binded to its own field name
        assertThat(bindingResult.getFieldError(lastName), notNullValue());
    }
    
    @Test
    public void whenPUTWithMissingNullParam_returnEntity() throws Exception {
        when(singleJPAEntityService.save(putEntity)).thenReturn(putEntity);

        MvcResult result = mvc.perform(
                put("/entity/1")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               .param(lastName, lastName)
               //not posting testDate non @NotNull
            )
           .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntity.class)))
           .andReturn();
        
        TestEntity returnedEntity = assertAndReturnModelAttributeOfType(result.getModelAndView(), "data", TestEntity.class);
        
        assertThat(returnedEntity, equalTo(baseEntity));
   }

    @Test
    public void whenDELETEEntity_returnEntity() throws Exception {
        when(singleJPAEntityService.delete(TestEntity.class, 1l)).thenReturn(baseEntity);

        MvcResult result = mvc.perform(
                delete("/entity/1")
               .accept(MediaType.APPLICATION_JSON)
            )
           .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntity.class)))
           .andReturn();
        
        TestEntity returnedEntity = assertAndReturnModelAttributeOfType(result.getModelAndView(), "data", TestEntity.class);
        
        assertThat(returnedEntity, equalTo(baseEntity));
   }

    @Test
    public void whenDELETEMissingEntity_returnNotFound() throws Exception {
        mvc.perform(
                delete("/entity/1")
            )
           .andExpect(status().isNotFound());
    }

    @Test
    public void whenDELETEWrongURL_returnMethodNotAllowed() throws Exception {
        mvc.perform(
                delete("/entity")
            )
           .andExpect(status().isMethodNotAllowed());

         mvc.perform(
                delete("/entity")
               .param("id", "1")//even giving the id as a param, mapping does not exists and should abort
            )
           .andExpect(status().isMethodNotAllowed());
   }

}