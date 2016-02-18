package org.books.api.api.entities;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.books.ejb.dto.OrderItemDTO;

@XmlRootElement(name="orderRequest")
@XmlType(propOrder = {"customerNr", "items"})
public class OrderRequest {

    private String customerNr;
    private List<OrderItemDTO> items;

    public OrderRequest() {
    }

    public OrderRequest(String customerNr, List<OrderItemDTO> items) {
        this.customerNr = customerNr;
        this.items = items;
    }

    public String getCustomerNr() {
        return customerNr;
    }

    public void setCustomerNr(String customerNr) {
        this.customerNr = customerNr;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

}
