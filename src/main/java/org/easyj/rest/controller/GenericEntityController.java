/*
 *  Copyright 2009-2012 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.easyj.rest.controller;

import java.util.Set;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.easyj.orm.SingleService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Abstract entity controller to be implemented with concrete
 * {@code @Service} implementation
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 * @deprecated Use {@code AbstractEntityController} instead
 */
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
