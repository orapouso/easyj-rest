package org.easyj.rest.controller;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public interface EntityController<E extends Serializable, ID> {
    
    public ModelAndView post(E entity, BindingResult result, HttpServletRequest request);
    
    public ModelAndView put(E entity, BindingResult result, HttpServletRequest request);
    
    public ModelAndView delete(ID primaryKey, HttpServletRequest request);
    
    public ModelAndView get(ID primaryKey);
    
    public ModelAndView getAll();
    
}
