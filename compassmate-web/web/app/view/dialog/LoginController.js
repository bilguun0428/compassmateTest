 

Ext.define('Compassmate.view.dialog.LoginController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.login',

    requires: [
        'Compassmate.view.dialog.Register',
    	'Compassmate.view.dialog.ResetPassword'
    ],

    /*init: function () {
        this.lookupReference('registerButton').setDisabled(
            !Compassmate.app.getServer().get('registration'));
        this.lookupReference('languageField').setValue(Locale.language);
    },*/

    login: function () {
        var form = this.lookupReference('form');
        if (form.isValid()) {
            Ext.get('spinner').setVisible(true);
            this.getView().setVisible(false);
            Ext.Ajax.request({
                scope: this,
                method: 'POST',
                url: 'api/session',
                params: form.getValues(),
                callback: function (options, success, response) {
                    var user, password;
                    Ext.get('spinner').setVisible(false);
                    if (success) {
                        if (this.lookupReference('rememberField').getValue()) {
                            user = Ext.util.Base64.encode(this.lookupReference('userField').getValue());
                            password = Ext.util.Base64.encode(this.lookupReference('passwordField').getValue());
                            Ext.util.Cookies.set('user', user, Ext.Date.add(new Date(), Ext.Date.YEAR, 1));
                            Ext.util.Cookies.set('password', password, Ext.Date.add(new Date(), Ext.Date.YEAR, 1));
                        }
                        Compassmate.app.setUser(Ext.decode(response.responseText));
                        this.fireViewEvent('login');
                    } else {
                        this.getView().setVisible(true);
                        if (response.status === 401) {
                            Compassmate.app.showError(Strings.loginFailed);
                        } else {
                            Compassmate.app.showError(response.responseText);
                        }
                    }
                }
            });
        }
    },

    logout: function () {
        Ext.util.Cookies.clear('user');
        Ext.util.Cookies.clear('password');
        Ext.Ajax.request({
            scope: this,
            method: 'DELETE',
            url: 'api/session',
            callback: function () {
                window.location.reload();
            }
        });
    },

    onSelectLanguage: function (selected) {
        var paramName, paramValue, url, prefix, suffix;
        paramName = 'locale';
        paramValue = selected.getValue();
        url = window.location.href;
        if (url.indexOf(paramName + '=') >= 0) {
            prefix = url.substring(0, url.indexOf(paramName));
            suffix = url.substring(url.indexOf(paramName));
            suffix = suffix.substring(suffix.indexOf('=') + 1);
            suffix = suffix.indexOf('&') >= 0 ? suffix.substring(suffix.indexOf('&')) : '';
            url = prefix + paramName + '=' + paramValue + suffix;
        } else if (url.indexOf('?') < 0) {
            url += '?' + paramName + '=' + paramValue;
        } else {
            url += '&' + paramName + '=' + paramValue;
        }
        window.location.href = url;
    },

    onAfterRender: function (field) {
        field.focus();
    },

    onSpecialKey: function (field, e) {
        if (e.getKey() === e.ENTER) {
            this.login();
        }
    },
    onResetPassword: function () {
    	Ext.create('Compassmate.view.dialog.ResetPassword').show();
    },

    onLoginClick: function () {
        Ext.getElementById('submitButton').click();
        this.login();
    },

    onRegisterClick: function () {
        Ext.create('Compassmate.view.dialog.Register').show();
    }
});
