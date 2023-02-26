package me.youhavetrouble.veto.endpoint.vatnumber;

import ch.digitalfondue.vatchecker.EUVatCheckResponse;
import ch.digitalfondue.vatchecker.EUVatChecker;
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
import java.util.regex.Matcher;

public class VatNumberEndpoint implements Endpoint {

    @Override
    public String path() {
        return "/v1/vatnumber";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI requestURI, @NotNull Headers headers, @NotNull Map<String, String> queryParams, @Nullable String requestBody) {
        if (!requestMethod.equals(RequestMethod.GET)) return JsonResponse.create("{}", 405);

        JSONObject response = new JSONObject();

        if (!queryParams.containsKey("number")) {
            response.put("message", "missing \"number\" parameter");
            return JsonResponse.create(response, 400);
        }
        if (!queryParams.containsKey("region")) {
            response.put("message", "missing \"region\" parameter");
            return JsonResponse.create(response, 400);
        }

        String region = queryParams.get("region").toUpperCase(Locale.ENGLISH);
        String number = queryParams.get("number");

        VatCountry vatCountry = VatCountry.valueOf(region);

        Matcher matcher = vatCountry.getRegex().matcher(number);
        if (!matcher.matches()) {
            response.put("message", "input is not a valid vat number for provided region");
            response.put("valid", false);
            return JsonResponse.create(response, 200);
        }

        EUVatCheckResponse viesResponse = EUVatChecker.doCheck(region, number);
        if (viesResponse.isError()) {
            response.put("message", "input is not a valid vat number for provided region");
            response.put("valid", false);
            return JsonResponse.create(response, 200);
        }

        response.put("valid", true);

        JSONObject validInfo = new JSONObject();
        validInfo.put("name", viesResponse.getName());
        validInfo.put("address", viesResponse.getAddress());

        response.put("info", validInfo);

        return JsonResponse.create(response, 200);
    }
}
