package org.books.web.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import org.books.web.data.entity.CreditCard;

@Named
@ApplicationScoped
public class CreditCardHelper implements Serializable {

    public CreditCard.Type[] getCardTypes() {
        return CreditCard.Type.values();
    }

    public List<SelectItem> getExpirationMonths() {
        List<SelectItem> monthItems = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            monthItems.add(new SelectItem(month, String.format("%02d", month)));
        }
        return monthItems;
    }

    public List<SelectItem> getExpirationYears() {
        List<SelectItem> yearItems = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year < currentYear + 5; year++) {
            yearItems.add(new SelectItem(year, String.format("%02d", year % 100)));
        }
        return yearItems;
    }
}
