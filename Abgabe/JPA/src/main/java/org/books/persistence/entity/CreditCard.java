package org.books.persistence.entity;

import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class CreditCard {

    public enum Type {

        MasterCard, Visa
    }

    private static final int MAX_CARD_EXPIRY_YEARS = 5;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    // @Convert(converter = AesEncryptorConverter.class)
    private String number;

    @Column(nullable = false)
    private Integer expirationMonth;

    @Column(nullable = false)
    private Integer expirationYear;

    public CreditCard() {
    }

    public CreditCard(Type type, String number, Integer expirationMonth, Integer expirationYear) {
        this.type = type;
        this.number = number;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
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

    public int getMinExpirationYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int getMaxExpirationYear() {
        return Calendar.getInstance().get(Calendar.YEAR) + MAX_CARD_EXPIRY_YEARS;
    }
}
