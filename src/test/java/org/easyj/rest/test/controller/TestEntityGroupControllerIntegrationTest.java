package org.easyj.rest.test.controller;

import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.config.PersistenceJPAConfig;
import org.easyj.rest.test.config.WebConfig;
import org.easyj.rest.test.domain.TestEntityGroup;
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

public class TestEntityGroupControllerIntegrationTest {

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
        mvc.perform(get("/entitygroup").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", empty()))
           .andExpect(model().attribute("result", nullValue()));

    }
    
    @Test
    public void whenGETMissingEntity_return404() throws Exception {
        mvc.perform(get("/entitygroup/1").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    public void whenPOSTNewEntity_returnEntityWithId() throws Exception {
        MvcResult result = mvc.perform(
                post("/entitygroup")
               .accept(MediaType.APPLICATION_JSON)
               .param("name", "name")
            )
           .andExpect(status().isOk())
           .andExpect(model().attribute("result", nullValue()))
           .andExpect(model().attribute("data", instanceOf(TestEntityGroup.class)))
           .andReturn();
        
        TestEntityGroup returnedEntity = (TestEntityGroup) result.getModelAndView().getModel().get("data");
        
        assertThat(returnedEntity.getId(), notNullValue());
        assertThat(returnedEntity.getId(), greaterThan(0l));
        
    }

}