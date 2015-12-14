/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.web.presentation;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.books.web.data.entity.CreditCard;
import org.books.web.presentation.util.MessageFactory;

/**
 *
 * @author cb
 */
@FacesValidator(CreditCardNumberValidator.CONVERTER_ID)
public class CreditCardNumberValidator implements Validator, StateHolder {

    public static final String CONVERTER_ID = "org.books.presentation.CreditCardNumberValidator";
    public static final String INVALID_FORMAT_MESSAGE = "org.books.presentation.CreditCardNumberValidator.INVALID_FORMAT_MESSAGE";
    public static final String INVALID_NUMBER_MESSAGE = "org.books.presentation.CreditCardNumberValidator.INVALID_NUMBER_MESSAGE";
    public static final String UNKNOWN_CARD_TYPE_MESSAGE = "org.books.presentation.CreditCardNumberValidator.UNKNOWN_CARD_TYPE_MESSAGE";

    private Boolean isTransient = false;
    private String cardTypeId;

    private final static Map<CreditCard.Type, SpecificCreditCardValidater> CREDITCARD_CARD_VALIDATORS = new ImmutableMap.Builder<CreditCard.Type, SpecificCreditCardValidater>()
            .put(CreditCard.Type.MasterCard, (String number) -> {
                if (number.length() != 16) {
                    FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, INVALID_FORMAT_MESSAGE);
                    throw new ValidatorException(message);
                }
                if (!number.matches("^5[1-5][0-9]{14}$")) {
                    FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, INVALID_NUMBER_MESSAGE);
                    throw new ValidatorException(message);
                }
            })
            .put(CreditCard.Type.Visa, (String number) -> {
                if (!((number.length() == 16) || (number.length() == 16))) {
                    FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, INVALID_FORMAT_MESSAGE);
                    throw new ValidatorException(message);
                }
                if (!number.matches("^4[0-9]{12}(?:[0-9]{3})?$")) {
                    FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, INVALID_NUMBER_MESSAGE);
                    throw new ValidatorException(message);
                }
            }).build();

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UIComponent cardType = component.getParent().findComponent(cardTypeId);
        if ((value instanceof String) && (cardType instanceof UIInput)) {
            String stringValue = (String) value;
            CreditCard.Type type = (CreditCard.Type) (((UIInput) cardType).getValue());
            validateCreditCard(stringValue, type);
        } else {
            FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, INVALID_FORMAT_MESSAGE);
            throw new ValidatorException(message);
        }
    }

    private void validateCreditCard(String cardNumber, CreditCard.Type cardType) throws ValidatorException {
        if (!cardNumber.endsWith(String.valueOf(getLuhn(cardNumber)))) {
            FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, INVALID_NUMBER_MESSAGE);
            throw new ValidatorException(message);
        }
        if (CREDITCARD_CARD_VALIDATORS.containsKey(cardType)) {
            CREDITCARD_CARD_VALIDATORS.get(cardType).validate(cardNumber);
        } else {
            FacesMessage message = MessageFactory.getMessage(FacesMessage.SEVERITY_ERROR, UNKNOWN_CARD_TYPE_MESSAGE);
            throw new ValidatorException(message);
        }
    }

    /**
     * Calculates Credit-Card specific LUHN (means only one Digit)
     *
     * @param data (Credit Card Number)
     * @return
     */
    private int getLuhn(String data) {
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

    public void setCardTypeId(String cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    @Override
    public Object saveState(FacesContext context) {
        return cardTypeId;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        this.cardTypeId = (String) state;
    }

    @Override
    public boolean isTransient() {
        return this.isTransient;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this.isTransient = newTransientValue;
    }

    private interface SpecificCreditCardValidater {

        void validate(String cardNumber) throws ValidatorException;
    }
}
