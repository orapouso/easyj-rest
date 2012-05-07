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

package org.easyj.rest.test.domain;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import org.easyj.rest.validation.groups.POSTChecks;
import org.easyj.rest.validation.groups.PUTChecks;

/**
 * Test {@code @Entity} for mapping tests
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 * @see TestEntityGroupController
 */
@Entity
public class TestEntityGroup implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(groups=PUTChecks.class)
    @Null(groups=POSTChecks.class)
    private Long id;
    @NotNull
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private Set<TestEntity> entities;

    public TestEntityGroup() {}
    
    public TestEntityGroup(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TestEntity> getEntities() {
        return entities;
    }

    public void setEntities(Set<TestEntity> entities) {
        this.entities = entities;
    }

}
