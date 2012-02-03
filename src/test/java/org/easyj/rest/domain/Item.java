package org.easyj.rest.domain;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Item implements Serializable {
    
    @EmbeddedId
    private ItemPK pk;
    
    private String name;
    private Integer quatity;
    private Integer price;
    @JoinColumn(name = "id_bill", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Bill bill;

    public ItemPK getPk() {
        return pk;
    }

    public void setPk(ItemPK pk) {
        this.pk = pk;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuatity() {
        return quatity;
    }

    public void setQuatity(Integer quatity) {
        this.quatity = quatity;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
