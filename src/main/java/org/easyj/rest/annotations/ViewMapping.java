package org.easyj.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewMapping {
    String value() default "";
    
    String entity() default "{}/entity";
    
    String form() default "{}/form";

    String list() default "{}/list";
}