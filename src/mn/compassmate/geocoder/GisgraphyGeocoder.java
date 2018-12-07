package mn.compassmate.geocoder;

import javax.json.JsonObject;

public class GisgraphyGeocoder extends JsonGeocoder {

    public GisgraphyGeocoder() {
        this("http://services.gisgraphy.com/reversegeocoding/search", 0);
    }

    public GisgraphyGeocoder(String url, int cacheSize) {
        super(url + "?format=json&lat=%f&lng=%f&from=1&to=1", cacheSize);
    }

    @Override
    public Address parseAddress(JsonObject json) {
        Address address = new Address();

        JsonObject result = json.getJsonArray("result").getJsonObject(0);

        if (result.containsKey("streetName")) {
            address.setStreet(result.getString("streetName"));
        }
        if (result.containsKey("city")) {
            address.setSettlement(result.getString("city"));
        }
        if (result.containsKey("state")) {
            address.setState(result.getString("state"));
        }
        if (result.containsKey("countryCode")) {
            address.setCountry(result.getString("countryCode"));
        }

        return address;
    }

}
