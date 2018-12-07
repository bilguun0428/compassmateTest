 

Ext.define('Compassmate.view.edit.DriversController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.drivers',

    requires: [
        'Compassmate.view.dialog.Driver',
        'Compassmate.model.Driver'
    ],

    objectModel: 'Compassmate.model.Driver',
    objectDialog: 'Compassmate.view.dialog.Driver',
    removeTitle: Strings.sharedDriver

});
