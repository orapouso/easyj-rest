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

}
