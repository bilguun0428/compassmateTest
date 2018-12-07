 

Ext.define('Compassmate.view.dialog.Device', {
    extend: 'Compassmate.view.dialog.BaseEdit',

    title: Strings.sharedDevice,

    items: {
        xtype: 'form',
        items: [{
            xtype: 'fieldset',
            title: Strings.sharedRequired,
            items: [{
                xtype: 'textfield',
                name: 'name',
                fieldLabel: Strings.sharedName,
                allowBlank: false
            }, {
                xtype: 'textfield',
                name: 'uniqueId',
                fieldLabel: Strings.deviceIdentifier,
                allowBlank: false
            }]
        }, {
            xtype: 'fieldset',
            title: Strings.sharedExtra,
            collapsible: true,
            collapsed: true,
            items: [{
                xtype: 'combobox',
                name: 'groupId',
                fieldLabel: Strings.groupParent,
                store: 'Groups',
                queryMode: 'local',
                displayField: 'name',
                valueField: 'id'
            }, {
                xtype: 'textfield',
                name: 'phone',
                fieldLabel: Strings.sharedPhone
            }, {
                xtype: 'textfield',
                name: 'model',
                fieldLabel: Strings.deviceModel
            }, {
                xtype: 'textfield',
                name: 'contact',
                fieldLabel: Strings.deviceContact
            }, {
                xtype: 'combobox',
                name: 'category',
                fieldLabel: Strings.deviceCategory,
                store: 'DeviceImages',
                queryMode: 'local',
                displayField: 'name',
                valueField: 'key',
                editable: false,
                listConfig: {
                    getInnerTpl: function () {
                        return '<table><tr valign="middle" ><td><div align="center" style="width:40px;height:40px;" >' +
                        '{[new XMLSerializer().serializeToString(Compassmate.DeviceImages.getImageSvg(' +
                        'Compassmate.Style.mapColorOnline, false, 0, values.key))]}</div></td>' +
                        '<td>{name}</td></tr></table>';
                    }
                }
            }]
        }]
    }
});
