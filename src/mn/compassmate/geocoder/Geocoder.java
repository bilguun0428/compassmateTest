package mn.compassmate.geocoder;

public interface Geocoder {

    interface ReverseGeocoderCallback {

        void onSuccess(String address);

        void onFailure(Throwable e);

    }

    void getAddress(AddressFormat format, double latitude, double longitude, ReverseGeocoderCallback callback);

}
