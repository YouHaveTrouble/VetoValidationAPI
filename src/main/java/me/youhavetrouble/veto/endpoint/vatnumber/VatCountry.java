package me.youhavetrouble.veto.endpoint.vatnumber;

import java.util.regex.Pattern;

public enum VatCountry {

    AT("^U\\d{9}$"),
    BE("^\\d{10}$"),
    BG("^\\d{9,10}$"),
    HR("^\\d{11}$"),
    CY("^\\d{8}[a-zA-Z]$"),
    CZ("^\\d{8,10}$"),
    DK("^\\d{8}$"),
    EE("^\\d{9}$"),
    FI("^\\d{8}$"),
    FR("^(?![IOio])[A-Za-z0-9]{2}\\d{9}$"),
    EL("^[a-zA-Z0-9]{1}\\d{7}[a-zA-Z0-9]{1}$"),
    NL("^\\d{9}B\\d{2}$"),
    IE("^\\d{1}[a-zA-z0-9\\+\\*]{1}\\d{5}[a-zA-Z]{1}$"),
    LT("^\\d{12}|\\d{9}$"),
    LU("^\\d{8}$"),
    LV("^\\d{11}$"),
    MT("^\\d{8}$"),
    DE("^\\d{9}$"),
    PL("^\\d{10}$"),
    PT("^\\d{9}$"),
    RO("^\\d{2,10}$"),
    SK("^\\d{10}$"),
    SI("^\\d{8}$"),
    SE("^\\d{12}$"),
    HU("^\\d{8}$"),
    IT("^\\d{11}$"),
    ;

    private final Pattern regex;
    VatCountry(String regex) {
        this.regex = Pattern.compile(regex);
    }

    public Pattern getRegex() {
        return regex;
    }

}
