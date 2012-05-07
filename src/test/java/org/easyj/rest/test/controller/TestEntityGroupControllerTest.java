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

package org.easyj.rest.test.controller;

import java.util.ArrayList;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.config.PersistenceJPAConfig;
import org.easyj.rest.test.config.WebConfig;
import org.easyj.rest.test.domain.TestEntityGroup;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.server.MockMvc;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests mappings and responses of {@code TestEntityGroupController}
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 * @see TestEntityGroupController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={ApplicationConfig.class, PersistenceJPAConfig.class})
public class TestEntityGroupControllerTest {

    @Autowired
    private ApplicationContext ac;
    
    private SingleJPAEntityService singleJPAEntityService;
    
    private static MockMvc mvc;
    
    private TestEntityGroup baseEntity = new TestEntityGroup();
        
    @Before
    public void before() {
        mvc = annotationConfigSetup(WebConfig.class).
                setParentContext(ac).build();
        
        singleJPAEntityService = ac.getBean(SingleJPAEntityService.class);
        reset(singleJPAEntityService);
        
        baseEntity.setId(1l);
    }
    
    @Test
    public void whenGETEntityWithNoId_returnAllEntities() throws Exception {
        when(singleJPAEntityService.findAll(TestEntityGroup.class))
                .thenReturn(new ArrayList<TestEntityGroup>())
                .thenReturn(new ArrayList<TestEntityGroup>(){{add(baseEntity); add(new TestEntityGroup(2l));}});

        mvc.perform(get("/entitygroup").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(model().attribute("data", empty()))
           .andExpect(model().attribute("result", nullValue()));

    }
    
    @Test
    public void whenGETMissingEntity_return404() throws Exception {
        mvc.perform(get("/entitygroup/15").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

}