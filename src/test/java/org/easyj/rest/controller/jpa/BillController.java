package org.easyj.rest.controller.jpa;

import org.easyj.rest.domain.Bill;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bill")
public class BillController extends JPAGenericEntityController<Bill, Long> {
    
}
