package org.easyj.rest.controller.jpa;

import java.io.Serializable;
import javax.annotation.Resource;
import org.easyj.orm.SingleService;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.controller.AbstractGenericEntityController;
import org.springframework.stereotype.Controller;

@Controller
public class JPAGenericEntityController <E extends Serializable, ID> extends AbstractGenericEntityController<E, ID> {

    @Resource(name="SingleJPAEntityService")
    @Override
    public void setService(SingleService service) {
        this.service = (SingleJPAEntityService) service;
    }

    @Override
    public SingleService getService() {
        return (SingleJPAEntityService) this.service;
    }

}
