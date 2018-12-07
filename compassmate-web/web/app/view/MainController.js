 
Ext.define('Compassmate.view.MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.mainController',

    init: function () {
        this.lookupReference('reportView').setHidden(Compassmate.app.getBooleanAttributePreference('ui.disableReport'));
    }
});
