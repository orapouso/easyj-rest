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
