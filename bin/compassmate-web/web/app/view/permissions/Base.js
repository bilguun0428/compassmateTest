 
Ext.define('Compassmate.view.permissions.Base', {
    extend: 'Ext.grid.Panel',

    requires: [
        'Compassmate.view.permissions.BaseController'
    ],

    controller: 'base',

    selModel: {
        selType: 'checkboxmodel',
        checkOnly: true,
        showHeaderCheckbox: false
    },

    listeners: {
        beforedeselect: 'onBeforeDeselect',
        beforeselect: 'onBeforeSelect'
    }
});
