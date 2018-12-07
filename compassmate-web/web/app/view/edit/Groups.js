 

Ext.define('Compassmate.view.edit.Groups', {
    extend: 'Ext.grid.Panel',
    xtype: 'groupsView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.AttributeFormatter',
        'Compassmate.view.edit.GroupsController',
        'Compassmate.view.edit.Toolbar'
    ],

    plugins: 'gridfilters',

    controller: 'groups',
    store: 'Groups',

    tbar: {
        xtype: 'editToolbar',
        items: [{
            xtype: 'button',
            disabled: true,
            handler: 'onGeofencesClick',
            reference: 'toolbarGeofencesButton',
            glyph: 'xf21d@FontAwesome',
            tooltip: Strings.sharedGeofences,
            tooltipType: 'title'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onAttributesClick',
            reference: 'toolbarAttributesButton',
            glyph: 'xf0ae@FontAwesome',
            tooltip: Strings.sharedComputedAttributes,
            tooltipType: 'title'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onDriversClick',
            reference: 'toolbarDriversButton',
            glyph: 'xf2c2@FontAwesome',
            tooltip: Strings.sharedDrivers,
            tooltipType: 'title'
        }]
    },

    listeners: {
        selectionchange: 'onSelectionChange'
    },

    columns: {
        defaults: {
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal
        },
        items: [{
            text: Strings.sharedName,
            dataIndex: 'name',
            filter: 'string'
        }, {
            text: Strings.groupDialog,
            dataIndex: 'groupId',
            hidden: true,
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'AllGroups'
            },
            renderer: Compassmate.AttributeFormatter.getFormatter('groupId')
        }]
    }
});
