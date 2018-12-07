package mn.compassmate.notification;

import mn.compassmate.Config;
import mn.compassmate.model.ExtendedModel;

public class PropertiesProvider {

    private Config config;

    private ExtendedModel extendedModel;

    public PropertiesProvider(Config config) {
        this.config = config;
    }

    public PropertiesProvider(ExtendedModel extendedModel) {
        this.extendedModel = extendedModel;
    }

    public String getString(String key) {
        if (config != null) {
            return config.getString(key);
        } else {
            return extendedModel.getString(key);
        }
    }

    public String getString(String key, String defaultValue) {
        String value = getString(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

}
