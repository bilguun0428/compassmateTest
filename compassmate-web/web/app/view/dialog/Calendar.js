
Ext.define('Compassmate.view.dialog.Calendar', {
    extend: 'Compassmate.view.dialog.BaseEdit',

    requires: [
        'Compassmate.view.dialog.CalendarController'
    ],

    controller: 'calendar',
    title: Strings.sharedCalendar,

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
                xtype: 'filefield',
                name: 'file',
                fieldLabel: Strings.sharedFile,
                allowBlank: false,
                buttonConfig: {
                    glyph: 'xf093@FontAwesome',
                    text: '',
                    tooltip: Strings.sharedSelectFile,
                    tooltipType: 'title',
                    minWidth: 0
                },
                listeners: {
                    change: 'onFileChange'
                }
            }]
        }, {
            xtype: 'hiddenfield',
            name: 'data',
            allowBlank: false,
            reference: 'dataField'
        }]
    }
});
