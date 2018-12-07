package mn.compassmate.geolocation;

import mn.compassmate.geolocation.MozillaGeolocationProvider;
import mn.compassmate.geolocation.GeolocationProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import mn.compassmate.BaseTest;
import mn.compassmate.model.CellTower;
import mn.compassmate.model.Network;

public class GeolocationProviderTest extends BaseTest {

    @Ignore
    @Test
    public void test() throws Exception {
        testLocationProvider();
    }

    public void testLocationProvider() throws Exception {
        MozillaGeolocationProvider provider = new MozillaGeolocationProvider(null);

        Network network = new Network(CellTower.from(208, 1, 2, 1234567));

        provider.getLocation(network, new GeolocationProvider.LocationProviderCallback() {
            @Override
            public void onSuccess(double latitude, double longitude, double accuracy) {
                Assert.assertEquals(60.07254, latitude, 0.00001);
                Assert.assertEquals(30.30996, longitude, 0.00001);
            }

            @Override
            public void onFailure(Throwable e) {
                Assert.fail();
            }
        });

        Thread.sleep(Long.MAX_VALUE);
    }

}
