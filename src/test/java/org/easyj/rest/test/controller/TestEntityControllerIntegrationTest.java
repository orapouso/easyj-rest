package org.easyj.rest.test.controller;

import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.config.PersistenceJPAConfig;
import org.easyj.rest.test.config.WebConfig;
import org.easyj.rest.test.domain.TestEntity;
import org.junit.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.BeforeClass;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;

public class TestEntityControllerIntegrationTest {

    private static MockMvc mvc;
    
    @BeforeClass
    public static void beforeClass() {
        mvc = annotationConfigSetup(new Class[]{
            ApplicationConfig.class,
            PersistenceJPAConfig.class,
            WebConfig.class
        }).build();
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
        mvc.perform(get("/entity/1").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
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