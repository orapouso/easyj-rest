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

package org.easyj.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps view names to common CRUD operations
 * <br><br>
 * Use {@code {}} to be replaced with the annotations {@code baseViewName}<br>
 * <br>
 * Ex: @ViewMapping(value="xpto", form="{}/editForm")<br>
 * In this case, the {@code baseViewName} is <b>xpto</b> and the form viewName is <b>xpto/editForm</b><br>
 * <br>
 * If you have a {@code InternalResourceViewResolver} of prefix {@code /WEB-INF/view/} and suffix of {@code .jsp}
 * the above mapping would resolve to {@code /WEB-INF/view/xpto/editForm.jsp} if a {@code Request} would call {@code /xpto/create}
 *
 * @author Rafael Raposo
 * @since 1.1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewMapping {
    /**
     * {@code baseViewName} generally used for organizing view files into directories.
     * don't edit if files should be in the same place<br>
     * defaults to {@code @RequestMapping} value
     * @return baseViewName
     */
    String value() default ":ROOT";
    
    /**
     * {@code viewName} to be returned on basic {@code GET html} requests<br>
     * defaults to {@code {}/entity}
     * @return entity viewName
     */
    String entity() default "{}/entity";
    
    /**
     * {@code viewName} to be returned on requests for editing or creating entities<br>
     * defaults to {@code {}/form}
     * @return form viewName
     */
    String form() default "{}/form";

    /**
     * {@code viewName} to be returned on requests for listing entities<br>
     * defaults to {@code {}/list}
     * @return list viewName
     */
    String list() default "{}/list";
}