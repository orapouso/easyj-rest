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
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import org.easyj.rest.validation.groups.POSTChecks;
import org.easyj.rest.validation.groups.PUTChecks;

/**
 * Test {@code @Entity} for mapping tests
 * 
 * @author Rafael Raposo
 * @since 1.1.0
 * @see TestEntityController
 */
@Entity
public class TestEntity implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(groups=PUTChecks.class)
    @Null(groups=POSTChecks.class)
    private Long id;
    @NotNull
    @Size(max=15)
    private String firstName;
    @NotNull
    @Size(max=15)
    private String lastName;
    @Temporal(TemporalType.TIMESTAMP)
    private Date testDate;
    @ManyToOne
    @JoinColumn(name="id_group")
    private TestEntityGroup group;

    public TestEntity() {}
    
    public TestEntity(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public TestEntityGroup getGroup() {
        return group;
    }

    public void setGroup(TestEntityGroup group) {
        this.group = group;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 53 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 53 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestEntity other = (TestEntity) obj;
        
        boolean idsNull = this.id == null || other.id == null;
        if(!idsNull) {
            return this.id.equals(other.id);
        } else {
            if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
                return false;
            }
            if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName)) {
                return false;
            }
            return true;
        }
    }

}
