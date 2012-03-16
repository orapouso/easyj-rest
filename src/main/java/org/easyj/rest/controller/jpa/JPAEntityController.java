package org.easyj.rest.controller.jpa;

import javax.annotation.Resource;
import org.easyj.orm.SingleService;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.controller.AbstractEntityController;

public class JPAEntityController extends AbstractEntityController {

    @Resource(name="singleJPAEntityService")
    @Override
    public void setService(SingleService service) {
        this.service = (SingleJPAEntityService) service;
    }

    @Override
    public SingleService getService() {
        return (SingleJPAEntityService) this.service;
    }

}
