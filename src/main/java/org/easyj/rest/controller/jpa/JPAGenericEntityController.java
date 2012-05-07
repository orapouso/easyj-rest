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

package org.easyj.rest.controller.jpa;

import java.io.Serializable;
import javax.annotation.Resource;
import org.easyj.orm.SingleService;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.controller.AbstractGenericEntityController;

/**
 * JPA implementation of {@code AbstractGenericEntityController}
 * <br><br>
 * Extend this controller with an {@code @Entity} to have basic CRUD funcionatity
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 */
public class JPAGenericEntityController <E extends Serializable, ID> extends AbstractGenericEntityController<E, ID> {

    @Resource(name="singleJPAEntityService")
    @Override
    public void setService(SingleService service) {
        this.service = (SingleJPAEntityService) service;
    }

    @Override
    public SingleService getService() {
        return (SingleJPAEntityService) this.service;
    }

}
