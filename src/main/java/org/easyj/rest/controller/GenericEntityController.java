package org.easyj.rest.controller;

import java.util.Set;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.easyj.orm.SingleService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public abstract class GenericEntityController extends GenericController {

    protected SingleService service;

    public abstract void setService(SingleService service);

    public abstract SingleService getService();

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
