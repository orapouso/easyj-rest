package org.easyj.spring.orm.controller;

import org.easyj.orm.EntityService;
import org.springframework.stereotype.Controller;

@Controller
public abstract class GenericEntityController extends GenericController {

    protected EntityService service;

    public abstract void setService(EntityService service);

    public abstract EntityService getService();

}
