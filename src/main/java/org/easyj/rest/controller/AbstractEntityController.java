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
    protected String entityViewName = "{}/entity";
    protected String formViewName = "{}/form";
    protected String listViewName = "{}/list";
    
    @PostConstruct
    public void initialize() {
        entityMapping = this.getClass().getAnnotation(RequestMapping.class).value()[0];
        
        ViewMapping annon = this.getClass().getAnnotation(ViewMapping.class);
        if(annon == null) {
            if(entityMapping.charAt(0) == '/') {
                baseViewName = entityMapping.substring(1);
            }
        } else {
            if(!annon.value().isEmpty()) {
                baseViewName = annon.value();
            }
            if(!annon.form().isEmpty()) {
                formViewName = annon.form();
            }
            if(!annon.entity().isEmpty()) {
                entityViewName = annon.entity();
            }
            if(!annon.list().isEmpty()) {
                listViewName = annon.list();
            }
        }
        formViewName = formViewName.replace("{}", baseViewName);
        entityViewName = entityViewName.replace("{}", baseViewName);
        listViewName = listViewName.replace("{}", baseViewName);
    }
    
}
