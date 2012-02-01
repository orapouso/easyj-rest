package org.easyj.rest.controller;

import java.io.Serializable;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public interface EntityController<E extends Serializable, ID> {
    
    public ModelAndView post(E entity, HttpServletResponse response, BindingResult result);
    
    public ModelAndView put(E entity, HttpServletResponse response, BindingResult result);
    
    public ModelAndView delete(ID primaryKey, HttpServletResponse response);
    
    public ModelAndView get(ID primaryKey, HttpServletResponse response);
    
    public ModelAndView getAll(HttpServletResponse response);
    
}
