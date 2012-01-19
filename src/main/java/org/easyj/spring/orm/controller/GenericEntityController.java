package org.easyj.spring.orm.controller;

import java.util.Set;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.easyj.orm.EntityService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Controller
public abstract class GenericEntityController extends GenericController {

    protected EntityService service;

    public abstract void setService(EntityService service);

    public abstract EntityService getService();

    @Resource
    protected Validator validator;

    public <T> void bindValidatorErrors(Set<ConstraintViolation<T>> violations, BindingResult result) {
        if(violations != null) {
            for(ConstraintViolation<T> violation : violations) {
                result.addError(new FieldError(result.getObjectName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), false, null, null, violation.getMessageTemplate()));
            }
        }
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

}
