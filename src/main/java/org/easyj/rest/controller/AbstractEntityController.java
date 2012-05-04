package org.easyj.rest.controller;

import javax.annotation.PostConstruct;
import org.easyj.orm.SingleService;
import org.easyj.rest.annotations.ViewMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public abstract class AbstractEntityController extends AbstractController {

    protected SingleService service;

    public abstract void setService(SingleService service);

    public abstract SingleService getService();

    protected String entityMapping;
    protected String baseViewName;
    protected String formViewName = "edit";
    
    @PostConstruct
    public void initialize() {
        entityMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        
        ViewMapping annon = this.getClass().getAnnotation(ViewMapping.class);
        if(annon == null) {
            if(entityMapping.charAt(0) == '/') {
                baseViewName = entityMapping.substring(1);
            }
        } else {
            if(!annon.viewName().isEmpty()) {
                baseViewName = annon.viewName();
            }
            if(!annon.form().isEmpty()) {
                formViewName = annon.form();
            }
        }
    }
    
}
