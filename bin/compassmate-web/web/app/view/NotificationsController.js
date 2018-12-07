 

Ext.define('Compassmate.view.NotificationsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.notificationsController',

    init: function () {
        this.getView().getStore().load({
            params: {
                userId: this.getView().user.getId()
            }
        });
    },

    onCheckChange: function (column, rowIndex) {
        var record = this.getView().getStore().getAt(rowIndex);
        Ext.Ajax.request({
            scope: this,
            url: 'api/users/notifications',
            jsonData: record.data,
            callback: function (options, success, response) {
                if (!success) {
                    Compassmate.app.showError(response);
                }
            }
        });
    }
});
