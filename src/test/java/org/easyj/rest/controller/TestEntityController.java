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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.easyj.rest.annotations.ViewMapping;
import org.easyj.rest.controller.jpa.JPAGenericEntityController;
import org.easyj.rest.test.domain.TestEntity;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Test implementation of {@code JPAGenericEntityController}
 * 
 * Implements controller for @Entity {@code TestEntity}
 * This controller is tested by {@code TestEntityControllerTest}
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
@Controller
@RequestMapping("/entity")
@ViewMapping(value="entity", edit="{}/edit")
public class TestEntityController extends JPAGenericEntityController<TestEntity, Long> {
    
    @InitBinder
    public void customizeConversions(WebDataBinder binder) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, "testDate", new CustomDateEditor(df, true));
    }
    
}
