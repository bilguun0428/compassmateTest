 

Ext.define('Compassmate.view.dialog.Login', {
    extend: 'Compassmate.view.dialog.Base',
    alias: 'widget.login',

    requires: [
        'Compassmate.view.dialog.LoginController'
    ],

    controller: 'login',

    header: false,
    closable: false,

    items: {
        xtype: 'form',
        reference: 'form',
        width: 315,
        height: 170,
/*
        autoEl: {
            tag: 'form',
            method: 'POST',
            action: 'fake-login.html',
            target: 'submitTarget'
        },
		*/

        items: [/*{
            xtype: 'image',
            src: 'logo.svg',
            alt: Strings.loginLogo,
            width: 180,
            height: 48,
            style: {
                display: 'block',
                margin: '10px auto 25px'
            }
        }, */{
            xtype: 'combobox',
            name: 'language',
            fieldLabel: Strings.loginLanguage,
            store: 'Languages',
            displayField: 'name',
            valueField: 'code',
            editable: false,
            submitValue: false,
            listeners: {
                select: 'onSelectLanguage'
            },
            reference: 'languageField'
        }, {
            xtype: 'textfield',
            name: 'email',
            reference: 'userField',
            fieldLabel: Strings.userEmail,
            allowBlank: false,
            enableKeyEvents: true,
            listeners: {
                specialKey: 'onSpecialKey',
                afterrender: 'onAfterRender'
            },
            inputAttrTpl: ['autocomplete="on" autocapitalize="none"']
        }, {
            xtype: 'textfield',
            name: 'password',
            reference: 'passwordField',
            fieldLabel: Strings.userPassword,
            inputType: 'password',
            allowBlank: false,
            enableKeyEvents: true,
            listeners: {
                specialKey: 'onSpecialKey'
            },
            inputAttrTpl: ['autocomplete="on"']
        }, {
            xtype: 'checkboxfield',
            inputValue: true,
            uncheckedValue: false,
            reference: 'rememberField',
            fieldLabel: Strings.userRemember
        }, {
            xtype: 'component',
            html: '<iframe id="submitTarget" name="submitTarget" style="display:none"></iframe>'
        }, {
            xtype: 'component',
            html: '<input type="submit" id="submitButton" style="display:none">'
        }]
    },

    buttons: [{
    	text: Strings.loginResetPassword,
    	handler: 'onResetPassword',
    	reference: 'resetPassword'
        
    }, /*{
        text: Strings.loginRegister,
        handler: 'onRegisterClick',
        reference: 'registerButton',

    },*/ {
        text: Strings.loginLogin,
        handler: 'onLoginClick'
    }]
});
