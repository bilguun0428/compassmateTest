
Ext.define('Compassmate.view.edit.ComputedAttributesController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.computedAttributes',

    requires: [
        'Compassmate.view.dialog.ComputedAttribute',
        'Compassmate.model.ComputedAttribute'
    ],

    objectModel: 'Compassmate.model.ComputedAttribute',
    objectDialog: 'Compassmate.view.dialog.ComputedAttribute',
    removeTitle: Strings.sharedComputedAttribute
});
