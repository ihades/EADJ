package org.books.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named
@ApplicationScoped
public class LocaleHelper implements Serializable {

    @PostConstruct
    public void init() {

    }

    public String[] getCountryCodes() {
        return Locale.getISOCountries();
    }

    public List<SelectItem> getCountries() {
        final List<SelectItem> result = new ArrayList<>();
        for (String countryCode : Locale.getISOCountries()) {
            Locale locale = new Locale(
                    FacesContext.getCurrentInstance().getViewRoot().getLocale().getCountry(),
                    countryCode);
            result.add(new SelectItem(locale.getCountry(), locale.getDisplayCountry()));
        }
        result.sort((SelectItem o1, SelectItem o2) -> ((String) o1.getLabel()).compareToIgnoreCase((String) o2.getLabel()));
        return result;
    }
}
