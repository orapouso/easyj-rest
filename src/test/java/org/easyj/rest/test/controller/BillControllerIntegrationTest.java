package org.easyj.rest.test.controller;

import org.easyj.rest.controller.GenericBillController;
import org.easyj.rest.test.config.ApplicationConfig;
import org.easyj.rest.test.config.PersistenceJPAConfig;
import org.easyj.rest.test.config.WebConfig;
import org.junit.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.*;

public class BillControllerIntegrationTest {

    @Test
    public void whenGETBillWithNoId_returnAllBills() throws Exception {

        annotationConfigSetup(new Class[]{ ApplicationConfig.class, PersistenceJPAConfig.class, WebConfig.class })
                .build()
                .perform(get("/billgeneric").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("data"));

    }

}