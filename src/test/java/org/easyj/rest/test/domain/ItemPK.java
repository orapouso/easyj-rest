package org.easyj.rest.test.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class ItemPK implements Serializable {
    
    @JoinColumn(name = "id_bill", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Bill bill;
    private Integer itemNumber;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

}
