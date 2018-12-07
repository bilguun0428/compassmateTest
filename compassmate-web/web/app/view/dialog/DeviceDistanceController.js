
Ext.define('Compassmate.view.dialog.DeviceDistanceController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.deviceDistance',

    onDeviceChange: function (combobox, newValue) {
        var position;
        this.lookupReference('setButton').setDisabled(newValue === null);
        if (newValue) {
            position = Ext.getStore('LatestPositions').findRecord('deviceId', newValue, 0, false, false, true);
            if (position) {
                this.lookupReference('totalDistance').setValue(position.get('attributes').totalDistance);
            }
        }
    },

    onSetClick: function (button) {
        var data = {};
        data.deviceId = this.lookupReference('deviceId').getValue();
        data.totalDistance = this.lookupReference('totalDistance').getValue();
        Ext.Ajax.request({
            scope: this,
            method: 'PUT',
            url: 'api/devices/' + data.deviceId + '/distance',
            jsonData: Ext.util.JSON.encode(data),
            callback: function (options, success, response) {
                if (!success) {
                    Compassmate.app.showError(response);
                }
            }
        });
        button.up('window').close();
    }
});
