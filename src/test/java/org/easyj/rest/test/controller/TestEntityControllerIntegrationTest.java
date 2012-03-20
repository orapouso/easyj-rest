package org.easyj.rest.test.controller;

import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.config.PersistenceJPAConfig;
import org.easyj.rest.test.config.WebConfig;
import org.easyj.rest.test.domain.TestEntity;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={ApplicationConfig.class, WebConfig.class, PersistenceJPAConfig.class})
public class TestEntityControllerIntegrationTest {

    private static MockMvc mvc;
    
    @Resource(name="singleJPAEntityService")
    private SingleJPAEntityService jpaService;
    
    @Autowired
    private WebApplicationContext wac;
    
    private TestEntity baseEntity = new TestEntity();
        
/*    @BeforeClass
    public static void beforeClass() {
        
        mvc = annotationConfigSetup(new Class[]{
            ApplicationConfig.class,
            PersistenceJPAConfig.class,
            WebConfig.class
        }).build();
        
    }*/
    
    @Before
    public void before() {
        baseEntity.setId(1l);
        baseEntity.setFirstName("firstName");
        baseEntity.setLastName("lastName");
        baseEntity.setTestDate(new Date());
        
        when(jpaService.save(anyObject())).thenReturn(baseEntity);
        when(jpaService.findAll(TestEntity.class)).thenReturn(new ArrayList<TestEntity>(){{add(new TestEntity(2l));}});
        //when(jpaService.findOne(TestEntity.class, 15l)).thenThrow(ResourceNotFoundException.class);
        when(jpaService.findOne(TestEntity.class, 1l)).thenReturn(baseEntity);

        mvc = MockMvcBuilders.webApplicationContextSetup(wac).build();
        
    }
    
    @Test
    public void whenGETEntityWithNoId_returnAllEntities() throws Exception {
        mvc.perform(get("/entity").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", empty()))
           .andExpect(model().attribute("result", nullValue()));

    }
    
    @Test
    public void whenGETMissingEntity_return404() throws Exception {
        mvc.perform(get("/entity/15").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    public void whenGETExistingEntity_returnEntity() throws Exception {
        MvcResult result = mvc.perform(get("/entity/1").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", notNullValue()))
           .andExpect(model().attribute("result", nullValue()))
           .andReturn();

        TestEntity returnedEntity = (TestEntity) result.getModelAndView().getModel().get("data");
        
        assertThat(returnedEntity.getId(), notNullValue());
        assertThat(returnedEntity.getId(), equalTo(1l));
    }

    @Test
    public void whenPOSTNewEntity_returnEntityWithId() throws Exception {
        String firstName = "firstName";
        String lastName = "lastName";
        
        MvcResult result = mvc.perform(
                post("/entity")
               .accept(MediaType.APPLICATION_JSON)
               .param(firstName, firstName)
               .param(lastName, lastName)
               .param("testDate", "20/12/1980")
            )
           .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntity.class)))
           .andReturn();
        
        TestEntity returnedEntity = (TestEntity) result.getModelAndView().getModel().get("data");
        
        assertThat(returnedEntity.getId(), notNullValue());
        assertThat(returnedEntity.getId(), greaterThan(0l));
        assertThat(returnedEntity.getFirstName(), equalTo(firstName));
        assertThat(returnedEntity.getLastName(), equalTo(lastName));
        
    }

}