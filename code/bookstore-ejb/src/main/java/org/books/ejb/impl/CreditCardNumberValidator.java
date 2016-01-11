/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.ejb.impl;

import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import org.books.ejb.exception.PaymentFailedException;
import org.books.persistence.entity.CreditCard;

/**
 *
 * @author cb
 */
public class CreditCardNumberValidator {

    private final static Map<CreditCard.Type, SpecificCreditCardValidater> CREDITCARD_CARD_VALIDATORS = new ImmutableMap.Builder<CreditCard.Type, SpecificCreditCardValidater>()
            .put(CreditCard.Type.MasterCard, new SpecificCreditCardValidater() {
                @Override
                public void validate(String number) throws PaymentFailedException {
                    if (number.length() != 16) {
                        throw new PaymentFailedException(PaymentFailedException.Code.INVALID_CREDIT_CARD);
                    }
                    if (!number.matches("^5[1-5][0-9]{14}$")) {
                        throw new PaymentFailedException(PaymentFailedException.Code.INVALID_CREDIT_CARD);
                    }
                }
            })
            .put(CreditCard.Type.Visa, new SpecificCreditCardValidater() {
                @Override
                public void validate(String number) throws PaymentFailedException {
                    if (!((number.length() == 16) || (number.length() == 16))) {
                        throw new PaymentFailedException(PaymentFailedException.Code.INVALID_CREDIT_CARD);
                    }
                    if (!number.matches("^4[0-9]{12}(?:[0-9]{3})?$")) {
                        throw new PaymentFailedException(PaymentFailedException.Code.INVALID_CREDIT_CARD);
                    }
                }
            }).build();

    private final BigDecimal limit;

    public CreditCardNumberValidator(BigDecimal limit) {
        this.limit = limit;
    }

    public final void validateCreditCard(CreditCard card, BigDecimal limit) throws PaymentFailedException {
        if (this.limit.compareTo(limit) != -1) {
            throw new PaymentFailedException(PaymentFailedException.Code.CREDIT_CARD_EXPIRED);
        }
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        if (!(month <= card.getExpirationMonth() && year <= card.getExpirationYear())) {
            throw new PaymentFailedException(PaymentFailedException.Code.CREDIT_CARD_EXPIRED);
        }
        if (!card.getNumber().endsWith(String.valueOf(getLuhn(card.getNumber())))) {
            throw new PaymentFailedException(PaymentFailedException.Code.INVALID_CREDIT_CARD);
        }
        if (CREDITCARD_CARD_VALIDATORS.containsKey(card.getType())) {
            CREDITCARD_CARD_VALIDATORS.get(card.getType()).validate(card.getNumber());
        } else {
            throw new PaymentFailedException(PaymentFailedException.Code.INVALID_CREDIT_CARD);
        }
    }

    /**
     * Calculates Credit-Card specific LUHN (means only one Digit)
     *
     * @param data (Credit Card Number)
     * @return
     */
    private final int getLuhn(String data) {
        int sum = 0;
        Boolean odd = false;
        for (int i = data.length() - 2; i >= 0; i--) {
            if (odd == false) {
                int tSum = Character.getNumericValue(data.charAt(i)) * 2;
                if (tSum >= 10) {
                    tSum = tSum - 9;
                }
                sum += tSum;
            } else {
                sum += Character.getNumericValue(data.charAt(i));
            }
            odd = !odd;
        }
        int erg = (((sum / 10) + 1) * 10) - sum;
        if (String.valueOf(erg).endsWith("0")) {
            erg = 0;
        }
        return erg;
    }

    private interface SpecificCreditCardValidater {

        void validate(String cardNumber) throws PaymentFailedException;
    }
}
