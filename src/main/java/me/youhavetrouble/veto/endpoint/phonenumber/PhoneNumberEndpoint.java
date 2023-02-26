package me.youhavetrouble.veto.endpoint.phonenumber;

import com.google.i18n.phonenumbers.*;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import me.youhavetrouble.jankwebserver.response.JsonResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

/**
 * Query params:<br>
 * "number" - phone number to check (must be url encoded)<br>
 * "region" - default region. (<a href="https://www.iso.org/iso-3166-country-codes.html">SLDR two-letter region code</a>)
 */
public class PhoneNumberEndpoint implements Endpoint {

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private final PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
    private final PhoneNumberToCarrierMapper carrierMapper = PhoneNumberToCarrierMapper.getInstance();

    @Override
    public String path() {
        return "/v1/phonenumber";
    }

    @Override
    public HttpResponse handle(
            @NotNull RequestMethod requestMethod,
            @NotNull URI uri,
            @NotNull Headers headers,
            @NotNull Map<String, String> queryParams,
            @Nullable String body
    ) {
        if (!requestMethod.equals(RequestMethod.GET)) return JsonResponse.create("{}", 405);

        JSONObject response = new JSONObject();

        if (!queryParams.containsKey("number")) {
            response.put("message", "missing \"number\" parameter");
            return JsonResponse.create(response, 400);
        }

        CharSequence rawNumber = new StringBuilder(queryParams.get("number"));
        String region = queryParams.getOrDefault("region", "").toUpperCase(Locale.ENGLISH);

        Phonenumber.PhoneNumber number;

        try {
            number = phoneNumberUtil.parse(rawNumber, region);
        } catch (NumberParseException e) {
            response.put("message", "could not parse phone number from input");
            response.put("valid", false);
            return JsonResponse.create(response, 200);
        }

        if (!phoneNumberUtil.isPossibleNumber(number)) {
            response.put("message", "input is not a possible phone number");
            response.put("valid", false);
            return JsonResponse.create(response, 200);
        }

        boolean isValid;

        if (!"".equals(region)) {
            isValid = phoneNumberUtil.isValidNumberForRegion(number, region);
        } else {
            isValid = phoneNumberUtil.isValidNumber(number);
        }

        response.put("valid", isValid);

        if (!isValid) return JsonResponse.create(response, 200);

        JSONObject validInfo = new JSONObject();

        PhoneNumberUtil.PhoneNumberType phoneNumberType = phoneNumberUtil.getNumberType(number);
        validInfo.put("type", phoneNumberType.toString().toLowerCase(Locale.ENGLISH));

        boolean canBeDialledInternationally = phoneNumberUtil.canBeInternationallyDialled(number);
        validInfo.put("canBeDialedInternationally", canBeDialledInternationally);

        String regionCode = phoneNumberUtil.getRegionCodeForNumber(number);
        validInfo.put("regionCode", regionCode);

        validInfo.put("country", geocoder.getDescriptionForValidNumber(number, Locale.ENGLISH));

        validInfo.put("carrier", carrierMapper.getNameForValidNumber(number, Locale.ENGLISH));

        response.put("info", validInfo);

        JSONObject formats = new JSONObject();

        formats.put("E164", phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164));
        formats.put("RFC3966", phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.RFC3966));
        formats.put("national", phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
        formats.put("international", phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));

        response.put("formats", formats);


        return JsonResponse.create(response, 200);
    }

}
