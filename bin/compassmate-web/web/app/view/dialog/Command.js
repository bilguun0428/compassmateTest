 

Ext.define('Compassmate.view.dialog.Command', {
    extend: 'Compassmate.view.dialog.Base',

    requires: [
        'Compassmate.view.dialog.CommandController'
    ],

    controller: 'command',
    title: Strings.commandTitle,

    items: {
        xtype: 'form',
        items: [{
            xtype: 'checkboxfield',
            name: 'textChannel',
            reference: 'textChannelCheckBox',
            inputValue: true,
            uncheckedValue: false,
            fieldLabel: Strings.notificationSms,
            listeners: {
                change: 'onTextChannelChange'
            }
        }, {
            xtype: 'combobox',
            name: 'type',
            reference: 'commandType',
            fieldLabel: Strings.sharedType,
            store: 'CommandTypes',
            displayField: 'name',
            valueField: 'type',
            editable: false,
            listeners: {
                select: 'onSelect'
            }
        }, {
            xtype: 'fieldcontainer',
            reference: 'paramPositionPeriodic',
            name: 'attributes',
            hidden: true,

            items: [{
                xtype: 'numberfield',
                fieldLabel: Strings.commandFrequency,
                name: 'frequency'
            }, {
                xtype: 'combobox',
                fieldLabel: Strings.commandUnit,
                name: 'unit',
                store: 'TimeUnits',
                displayField: 'name',
                valueField: 'factor'
            }]
        }, {
            xtype: 'fieldcontainer',
            reference: 'paramOutputControl',
            name: 'attributes',
            hidden: true,

            items: [{
                xtype: 'numberfield',
                fieldLabel: Strings.commandIndex,
                name: 'index',
                allowBlank: false
            }, {
                xtype: 'textfield',
                fieldLabel: Strings.commandData,
                name: 'data'
            }]
        }, {
            xtype: 'fieldcontainer',
            reference: 'paramSendSmsUssd',
            name: 'attributes',
            hidden: true,

            items: [{
                xtype: 'textfield',
                fieldLabel: Strings.commandPhone,
                name: 'phone'
            }, {
                xtype: 'textfield',
                reference: 'paramSmsMessage',
                fieldLabel: Strings.commandMessage,
                name: 'message',
                hidden: true
            }]
        }, {
            xtype: 'fieldcontainer',
            reference: 'paramSetTimezone',
            name: 'attributes',
            hidden: true,

            items: [{
                xtype: 'numberfield',
                fieldLabel: Strings.commandTimezone,
                name: 'timezone',
                minValue: -12,
                step: 0.5,
                maxValue: +14
            }]
        }, {
            xtype: 'fieldcontainer',
            reference: 'paramSetIndicator',
            name: 'attributes',
            hidden: true,

            items: [{
                xtype: 'numberfield',
                fieldLabel: Strings.commandData,
                name: 'data',
                minValue: 0,
                maxValue: 99
            }]
        }, {
            xtype: 'textfield',
            reference: 'paramCustom',
            fieldLabel: Strings.commandCustom,
            name: 'customCommand',
            hidden: true,
            allowBlank: false
        }]
    },

    buttons: [{
        text: Strings.commandSend,
        handler: 'onSendClick'
    }, {
        text: Strings.sharedCancel,
        handler: 'closeView'
    }]
});
