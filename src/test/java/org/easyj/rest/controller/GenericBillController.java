package org.easyj.rest.controller;

import org.easyj.rest.controller.jpa.JPAGenericEntityController;
import org.easyj.rest.test.domain.Bill;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/billgeneric")
public class GenericBillController extends JPAGenericEntityController<Bill, Long> {
    
}
