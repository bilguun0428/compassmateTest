 

Ext.define('Compassmate.view.dialog.Base', {
    extend: 'Ext.window.Window',

    bodyPadding: Compassmate.Style.normalPadding,
    resizable: false,
    autoScroll: true,
    constrain: true,

    initComponent: function () {
        if (window.innerHeight) {
            this.maxHeight = window.innerHeight - Compassmate.Style.normalPadding * 2;
        }
        this.callParent();
    }
});
