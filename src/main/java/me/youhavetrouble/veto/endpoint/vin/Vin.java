package me.youhavetrouble.veto.endpoint.vin;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO region and country parsing
public class Vin {

    private static final Pattern vinPattern = Pattern.compile("^(?<wmi>[0-9A-HJ-NPR-Z]{3})(?<vds>[0-9A-HJ-NPR-Z]{6})(?<vis>[0-9A-HJ-NPR-Z]{8})$");
    private static final Map<String, String> manufacturers = new HashMap<>();
    private static final Map<Integer, String> years = new HashMap<>();

    static {
        try (InputStream is = Vin.class.getClassLoader().getResourceAsStream("vin/manufacturers.json")) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String raw = s.hasNext() ? s.next() : "";
            JSONObject data = new JSONObject(raw);
            data.toMap().forEach((key, value) -> manufacturers.put(key, (String) value));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
        try (InputStream is = Vin.class.getClassLoader().getResourceAsStream("vin/years.json")) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String raw = s.hasNext() ? s.next() : "";
            JSONObject data = new JSONObject(raw);
            data.toMap().forEach((key, value) -> years.put(Integer.parseInt(key), (String) value));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }

    private final String vin;
    private final String manufacturer;
    private final List<Integer> possibleYears = new ArrayList<>(2);

    protected Vin(String rawVin) {
        Matcher matcher = vinPattern.matcher(rawVin);
        if (!matcher.find()) throw new InvalidVinException();

        this.vin = rawVin;
        String wmi = matcher.group("wmi");
//        String vds = matcher.group("vds");
        String vis = matcher.group("vis");

        this.manufacturer = wmi != null ? manufacturers.get(wmi) : null;

        int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;

        years.forEach((year, value) -> {
            if (year >= nextYear) return;
            if (!Objects.equals(value, vis.substring(0, 1))) return;
            this.possibleYears.add(year);
        });

    }

    protected String getVin() {
        return vin;
    }

    protected String getManufacturer() {
        return this.manufacturer;
    }

    protected List<Integer> getPossibleYears() {
        return this.possibleYears;
    }

}
