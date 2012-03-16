package org.easyj.rest.controller;

import org.easyj.rest.controller.jpa.JPAEntityController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/bill")
public class BillController extends JPAEntityController {
    
    @RequestMapping(method= RequestMethod.GET)
    public ModelAndView get(ModelAndView mav) {
        mav.setViewName("bill");
        return mav;
    }
    
}
