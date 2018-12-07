package mn.compassmate.geocoder;

import javax.json.JsonObject;

public class GeocodeFarmGeocoder extends JsonGeocoder {

    private static String formatUrl(String key, String language) {
        String url = "https://www.geocode.farm/v3/json/reverse/";
        url += "?lat=%f&lon=%f&country=us&count=1";
        if (key != null) {
            url += "&key=" + key;
        }
        if (language != null) {
            url += "&lang=" + language;
        }
        return url;
    }
    public GeocodeFarmGeocoder(String key, String language, int cacheSize) {
        super(formatUrl(key, language), cacheSize);
    }

    @Override
    public Address parseAddress(JsonObject json) {
        Address address = new Address();

        JsonObject result = json
                .getJsonObject("geocoding_results")
                .getJsonArray("RESULTS")
                .getJsonObject(0)
                .getJsonObject("ADDRESS");

        if (result.containsKey("street_number")) {
            address.setStreet(result.getString("street_number"));
        }
        if (result.containsKey("street_name")) {
            address.setStreet(result.getString("street_name"));
        }
        if (result.containsKey("locality")) {
            address.setSettlement(result.getString("locality"));
        }
        if (result.containsKey("admin_1")) {
            address.setState(result.getString("admin_1"));
        }
        if (result.containsKey("country")) {
            address.setCountry(result.getString("country"));
        }

        return address;
    }

}
