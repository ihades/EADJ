package org.books.persistence.testdata;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;

public enum OrderData {

    ORDER_1("1111-001", BigDecimal.valueOf(158.60), Date.from(LocalDateTime.of(2010, Month.JANUARY, 25, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_2("1111-001", BigDecimal.valueOf(158.60), Date.from(LocalDateTime.of(2010, Month.JANUARY, 25, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_3("1111-002", BigDecimal.valueOf(33.55), Date.from(LocalDateTime.of(2010, Month.JANUARY, 25, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_4("1111-003", BigDecimal.valueOf(16.99), Date.from(LocalDateTime.of(2010, Month.JANUARY, 25, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_5("1111-004", BigDecimal.valueOf(77.20), Date.from(LocalDateTime.of(2011, Month.JUNE, 15, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_6("2222-001", BigDecimal.valueOf(28.15), Date.from(LocalDateTime.of(2014, Month.SEPTEMBER, 23, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_7("2222-001", BigDecimal.valueOf(28.15), Date.from(LocalDateTime.of(2014, Month.SEPTEMBER, 23, 0, 0).toInstant(ZoneOffset.UTC))),
    ORDER_8("3333-001", BigDecimal.valueOf(77.10), Date.from(LocalDateTime.of(2014, Month.SEPTEMBER, 23, 0, 0).toInstant(ZoneOffset.UTC)));

    private final String number;
    private final Date date;
    private final BigDecimal amount;

    private OrderData(String number, BigDecimal amount, Date date) {
        this.number = number;
        this.date = date;
        this.amount = amount;
    }

    public String number() {
        return this.number;
    }

    public Date date() {
        return this.date;
    }

    public BigDecimal amount() {
        return this.amount;
    }

}
