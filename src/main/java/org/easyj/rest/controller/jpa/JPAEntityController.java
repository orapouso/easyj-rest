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

import javax.annotation.Resource;
import org.easyj.orm.SingleService;
import org.easyj.orm.jpa.SingleJPAEntityService;
import org.easyj.rest.controller.AbstractEntityController;

/**
 * JPA implementation of {@code AbstractEntityController}
 * <br><br>
 * Extend this controller to implement your own CRUD method mappings
 * 
 * @author Rafael Raposo
 * @since 1.0.0
 */
public class JPAEntityController extends AbstractEntityController {

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
