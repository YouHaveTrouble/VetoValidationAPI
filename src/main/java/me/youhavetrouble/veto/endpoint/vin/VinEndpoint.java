package me.youhavetrouble.veto.endpoint.vin;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import me.youhavetrouble.jankwebserver.response.JsonResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;


public class VinEndpoint implements Endpoint {

    @Override
    public String path() {
        return "/v1/vin";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI requestURI, @NotNull Headers headers, @NotNull Map<String, String> queryParams, @Nullable String requestBody) {
        if (!requestMethod.equals(RequestMethod.GET)) return JsonResponse.create("{}", 405);

        JSONObject response = new JSONObject();

        if (!queryParams.containsKey("vin")) {
            response.put("message", "missing \"vin\" parameter");
            return JsonResponse.create(response, 400);
        }

        String rawVin = queryParams.get("vin");
        Vin vin;
        try {
            vin = new Vin(rawVin);
        } catch (InvalidVinException e) {
            response.put("valid", false);
            return JsonResponse.create(response, 200);
        }
        response.put("valid", true);
        JSONObject validInfo = new JSONObject();
        validInfo.put("manufacturer", vin.getManufacturer());
        validInfo.put("possibleYearOfProduction", vin.getPossibleYears());

        response.put("info", validInfo);

        return JsonResponse.create(response, 200);
    }
}
