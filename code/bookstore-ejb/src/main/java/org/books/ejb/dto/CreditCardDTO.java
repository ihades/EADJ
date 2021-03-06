package org.books.ejb.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlRootElement(name = "creditCard")
@XmlType(propOrder = {"type", "number", "expirationMonth", "expirationYear"})
public class CreditCardDTO implements Serializable {

    @XmlType(name = "type")
    @XmlEnum
    public enum Type {

        MasterCard, Visa
    }
    private Type type;
    private String number;
    private Integer expirationMonth;
    private Integer expirationYear;

    public CreditCardDTO() {
    }

    public CreditCardDTO(Type type, String number, Integer expirationMonth, Integer expirationYear) {
        this.type = type;
        this.number = number;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
    }

    public CreditCardDTO(CreditCardDTO other) {
        this.type = other.type;
        this.number = other.number;
        this.expirationMonth = other.expirationMonth;
        this.expirationYear = other.expirationYear;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }
}
