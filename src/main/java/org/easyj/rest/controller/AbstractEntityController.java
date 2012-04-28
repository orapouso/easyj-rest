package org.easyj.rest.controller;

import org.easyj.orm.SingleService;

public abstract class AbstractEntityController extends AbstractController {

    protected SingleService service;

    public abstract void setService(SingleService service);

    public abstract SingleService getService();

}
