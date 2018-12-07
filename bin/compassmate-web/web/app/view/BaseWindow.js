 

Ext.define('Compassmate.view.BaseWindow', {
    extend: 'Ext.window.Window',

    width: Compassmate.Style.windowWidth,
    height: Compassmate.Style.windowHeight,
    layout: 'fit',

    initComponent: function () {
        if (window.innerWidth < Compassmate.Style.windowWidth || window.innerHeight < Compassmate.Style.windowHeight) {
            this.maximized = true;
            this.style = 'border-width: 0';
        }
        this.callParent();
    }
});
