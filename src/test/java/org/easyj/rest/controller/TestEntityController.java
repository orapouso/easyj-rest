package org.easyj.rest.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.easyj.rest.controller.jpa.JPAGenericEntityController;
import org.easyj.rest.test.domain.TestEntity;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/entity")
public class TestEntityController extends JPAGenericEntityController<TestEntity, Long> {
    
    @InitBinder
    public void customizeConversions(WebDataBinder binder) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, "testDate", new CustomDateEditor(df, true));
    }
    
}
