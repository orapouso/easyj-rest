package org.easyj.rest.controller;

import org.easyj.rest.controller.jpa.JPAGenericEntityController;
import org.easyj.rest.test.domain.TestEntityGroup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/entitygroup")
public class TestEntityGroupController extends JPAGenericEntityController<TestEntityGroup, Long> {
        
}
