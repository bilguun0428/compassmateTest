 

Ext.define('Compassmate.AttributeFormatter', {
    singleton: true,

    numberFormatterFactory: function (precision, suffix) {
        return function (value) {
            if (value !== undefined) {
                return Number(value.toFixed(precision)) + ' ' + suffix;
            }
            return null;
        };
    },

    coordinateFormatter: function (key, value) {
        return Ext.getStore('CoordinateFormats').formatValue(key, value, Compassmate.app.getPreference('coordinateFormat'));
    },

    speedFormatter: function (value) {
        return Ext.getStore('SpeedUnits').formatValue(value, Compassmate.app.getPreference('speedUnit'));
    },

    speedConverter: function (value) {
        return Ext.getStore('SpeedUnits').convertValue(value, Compassmate.app.getPreference('speedUnit'));
    },

    courseFormatter: function (value) {
        var courseValues = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
        return courseValues[Math.floor(value / 45)];
    },

    distanceFormatter: function (value) {
        return Ext.getStore('DistanceUnits').formatValue(value, Compassmate.app.getPreference('distanceUnit'));
    },

    distanceConverter: function (value) {
        return Ext.getStore('DistanceUnits').convertValue(value, Compassmate.app.getPreference('distanceUnit'));
    },

    durationFormatter: function (value) {
        var hours, minutes;
        hours = Math.floor(value / 3600000);
        minutes = Math.floor(value % 3600000 / 60000);
        return hours + ' ' + Strings.sharedHourAbbreviation + ' ' + minutes + ' ' + Strings.sharedMinuteAbbreviation;
    },
    
    geofenceDurationFormatter: function (value) {
        var hours, minutes, seconds;
        hours = Math.floor(value / 3600000);
        minutes = Math.floor(value % 3600000 / 60000);
        seconds = Math.floor(value % 3600000 / 60000);
        return hours + ' ' + Strings.sharedHourAbbreviation + ' ' + minutes + ' ' + Strings.sharedMinuteAbbreviation + ' ' + seconds + ' ' + Strings.sharedSecondAbbreviation;
    },

    deviceIdFormatter: function (value) {
        return Ext.getStore('Devices').getById(value).get('name');
    },

    groupIdFormatter: function (value) {
        var group, store;
        if (value !== 0) {
            store = Ext.getStore('AllGroups');
            if (store.getTotalCount() === 0) {
                store = Ext.getStore('Groups');
            }
            group = store.getById(value);
            return group ? group.get('name') : value;
        }
        return null;
    },

    geofenceIdFormatter: function (value) {
        var geofence, store;
        if (value !== 0) {
            store = Ext.getStore('AllGeofences');
            if (store.getTotalCount() === 0) {
                store = Ext.getStore('Geofences');
            }
            geofence = store.getById(value);
            return geofence ? geofence.get('name') : '';
        }
        return null;
    },

    driverUniqueIdFormatter: function (value) {
        var driver, store;
        if (value !== 0) {
            store = Ext.getStore('AllDrivers');
            if (store.getTotalCount() === 0) {
                store = Ext.getStore('Drivers');
            }
            driver = store.findRecord('uniqueId', value, 0, false, true, true);
            return driver ? value + ' (' + driver.get('name') + ')' : value;
        }
        return null;
    },

    lastUpdateFormatter: function (value) {
	if (value == null) return null;

        if (value instanceof Date) {
            if (Compassmate.app.getPreference('twelveHourFormat', false)) {
                return Ext.Date.format(value, Compassmate.Style.dateTimeFormat12);
            } else {
                return Ext.Date.format(value, Compassmate.Style.dateTimeFormat24);
            }
        }
        return value;
    },

    defaultFormatter: function (value) {
        if (typeof value === 'number') {
            return Number(value.toFixed(Compassmate.Style.numberPrecision));
        } else if (typeof value === 'boolean') {
            return value ? Ext.Msg.buttonText.yes : Ext.Msg.buttonText.no;
        } else if (value instanceof Date) {
            if (Compassmate.app.getPreference('twelveHourFormat', false)) {
                return Ext.Date.format(value, Compassmate.Style.dateTimeFormat12);
            } else {
                return Ext.Date.format(value, Compassmate.Style.dateTimeFormat24);
            }
        }
        return value;
    },

    getFormatter: function (key) {
        var self = this;

        switch (key) {
            case 'latitude':
            case 'longitude':
                return function (value) {
                    return self.coordinateFormatter(key, value);
                };
            case 'speed':
                return this.speedFormatter;
            case 'course':
                return this.courseFormatter;
            case 'distance':
            case 'accuracy':
                return this.distanceFormatter;
            case 'duration':
                return this.durationFormatter;
            case 'geofenceDuration':
                return this.geofenceDurationFormatter;
            case 'deviceId':
                return this.deviceIdFormatter;
            case 'groupId':
                return this.groupIdFormatter;
            case 'geofenceId':
                return this.geofenceIdFormatter;
            case 'lastUpdate':
                return this.lastUpdateFormatter;
            case 'spentFuel':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, Strings.sharedLiterAbbreviation);
            case 'chargedFuel':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, Strings.sharedLiterAbbreviation);
            case 'driverUniqueId':
                return this.driverUniqueIdFormatter;
            default:
                return this.defaultFormatter;
        }
    },

    getConverter: function (key) {
        switch (key) {
            case 'speed':
                return this.speedConverter;
            case 'distance':
            case 'accuracy':
                return this.distanceConverter;
            default:
                return function (value) {
                    return value;
                };
        }
    },

    getAttributeFormatter: function (key) {
        var dataType = Ext.getStore('PositionAttributes').getAttributeDataType(key);

        switch (dataType) {
            case 'distance':
                return this.distanceFormatter;
            case 'speed':
                return this.speedFormatter;
            case 'driverUniqueId':
                return this.driverUniqueIdFormatter;
            case 'voltage':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, Strings.sharedVoltAbbreviation);
            case 'percentage':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, '&#37;');
            case 'temperature':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, '&deg;C');
            case 'volume':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, Strings.sharedLiterAbbreviation);
            case 'consumption':
                return this.numberFormatterFactory(Compassmate.Style.numberPrecision, Strings.sharedLiterPerHourAbbreviation);
            default:
                return this.defaultFormatter;
        }
    },

    getAttributeConverter: function (key) {
        var dataType = Ext.getStore('PositionAttributes').getAttributeDataType(key);

        switch (dataType) {
            case 'distance':
                return this.distanceConverter;
            case 'speed':
                return this.speedConverter;
            default:
                return function (value) {
                    return value;
                };
        }
    }
});
