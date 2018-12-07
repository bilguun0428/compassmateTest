package mn.compassmate.geolocation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import mn.compassmate.Context;
import mn.compassmate.model.Network;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class UniversalGeolocationProvider implements GeolocationProvider {

    private String url;

    public UniversalGeolocationProvider(String url, String key) {
        this.url = url + "?key=" + key;
    }

    @Override
    public void getLocation(Network network, final LocationProviderCallback callback) {
        try {
            String request = Context.getObjectMapper().writeValueAsString(network);
            Context.getAsyncHttpClient().preparePost(url)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(request.length()))
                    .setBody(request).execute(new AsyncCompletionHandler() {
                @Override
                public Object onCompleted(Response response) throws Exception {
                    try (JsonReader reader = Json.createReader(response.getResponseBodyAsStream())) {
                        JsonObject json = reader.readObject();
                        if (json.containsKey("error")) {
                            callback.onFailure(
                                    new GeolocationException(json.getJsonObject("error").getString("message")));
                        } else {
                            JsonObject location = json.getJsonObject("location");
                            callback.onSuccess(
                                    location.getJsonNumber("lat").doubleValue(),
                                    location.getJsonNumber("lng").doubleValue(),
                                    json.getJsonNumber("accuracy").doubleValue());
                        }
                    }
                    return null;
                }

                @Override
                public void onThrowable(Throwable t) {
                    callback.onFailure(t);
                }
            });
        } catch (JsonProcessingException e) {
            callback.onFailure(e);
        }
    }

}
