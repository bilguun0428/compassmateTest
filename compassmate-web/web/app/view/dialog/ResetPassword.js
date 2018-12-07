 

Ext.define('Compassmate.view.dialog.ResetPassword', {
    extend: 'Compassmate.view.dialog.Base',

    requires: [
        'Compassmate.view.dialog.ResetPasswordController'
    ],

    controller: 'resetPassword',

    title: Strings.loginResetPassword,

    items: {
        xtype: 'form',
        reference: 'form',
        jsonSubmit: true,

        items: [{
            xtype: 'textfield',
            name: 'email',
            fieldLabel: Strings.userEmail1,
            validator: function (val) {
                if (/(.+)@(.+)\.(.{2,})/.test(val)) {
                    return true;
                } else {
                    return Ext.form.field.VTypes.emailText;
                }
            },
            allowBlank: false
        }]
    },

    buttons: [{
    	text: Strings.sharedCancel,
        handler: 'closeView'
    }, {
        text: Strings.loginResetPasswordSend,
        handler: 'onResetPassword'
    }]
});
