package org.easyj.rest.controller;

import java.io.Serializable;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public interface EntityController<E extends Serializable, ID> {
    
    public ModelAndView post(E entity, BindingResult result);
    
    public ModelAndView put(E entity, BindingResult result);
    
    public ModelAndView delete(ID primaryKey);
    
    public ModelAndView get(ID primaryKey);
    
    public ModelAndView getAll();
    
}
