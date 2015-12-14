/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.web.presentation;

import com.google.common.base.Splitter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author cb
 */
@FacesConverter(CreditCardNumberConverter.CONVERTER_ID)
public class CreditCardNumberConverter implements Converter {

    public static final String CONVERTER_ID = "org.books.presentatio.CreditCardNumberConverter";

    private static final int PARTITION_SIZE = 4;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        value = value.trim().replaceAll("\\s+", "").replaceAll("-", "");
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof String) {
            String stringValue = (String) value;
            return addBlanks(stringValue);
        } else {
            return value.toString();
        }
    }

    private String addBlanks(String string) {
        StringBuilder builder = new StringBuilder();
        String blank = "";
        for (String part : Splitter.fixedLength(PARTITION_SIZE).split(string)) {
            builder.append(blank);
            blank = " ";
            builder.append(part);
        }
        return builder.toString();
    }
}
