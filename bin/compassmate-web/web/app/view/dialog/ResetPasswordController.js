 

Ext.define('Compassmate.view.dialog.ResetPasswordController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.resetPassword',

//    onResetPassword: function () {
//        var form = this.lookupReference('form');
//        Ext.onReady(function() { 
//            Ext.apply(Ext.form.field.VTypes, {
//                password: function(val, field) {
//                    if (/^[a-z0-9]+$/i.test(val)) {
//                        return true;
//                    }
//                },
//                passwordText: 'Password may only contain letters and numbers.'
//            });
//            
//            Ext.QuickTips.init();
//            
//            function submitOnEnter(field, event) {
//                if (event.getKey() == event.ENTER) {
//                    var form = field.up('form').getForm();
//                    form.submit();
//                }
//            }
//            
//            // From bit.ly/Bvvv8
//            function password(length, special) {
//                var iteration = 0;
//                var password = '';
//                var randomNumber;
//
//                if (special == undefined) {
//                    var special = false;
//                }
//
//                while (iteration < length) {
//                    randomNumber = (Math.floor((Math.random() * 100)) % 94) + 33;
//                    if (!special) {
//                        if ((randomNumber >=33) && (randomNumber <=47)) { continue; }
//                        if ((randomNumber >=58) && (randomNumber <=64)) { continue; }
//                        if ((randomNumber >=91) && (randomNumber <=96)) { continue; }
//                        if ((randomNumber >=123) && (randomNumber <=126)) { continue; }
//                    }
//                    iteration++;
//                    password += String.fromCharCode(randomNumber);
//                }
//                return password;
//            }
//
//            // Form
//            // -----------------------------------------------------------------------
//            var addUserForm = Ext.create('Ext.form.Panel', {
//                renderTo: Ext.getBody(),
//                bodyStyle: 'padding: 5px 5px 0 5px;',
//                defaults: {
//                    xtype: 'textfield',
//                    anchor: '100%',
//                 },
//                items: [{
//                    fieldLabel: 'Email',
//                    name: 'email',
//                    vtype: 'email',
//                    maxLength: 64,            
//                    allowBlank: false,
//                    listeners: {
//                        specialkey: submitOnEnter
//                    }
//                },{
//                    xtype: 'fieldcontainer',
//                    fieldLabel: 'Password',
//                    layout: 'hbox',
//                    items: [{
//                        xtype: 'textfield',
//                        name: 'password',
//                        flex: 1,
//                        vtype: 'password',
//                        minLength: 4,
//                        maxLength: 32,
//                        allowBlank: false,
//                        listeners: {
//                            specialkey: submitOnEnter
//                        }
//                    }, {
//                        xtype: 'button',
//                        text: 'Random',
//                        tooltip: 'Generate a random password',
//                        style: 'margin-left: 4px;',
//                        flex: 0,
//                        handler: function() {
//                            this.prev().setValue(password(8, false));
//                            this.prev().focus()
//                        }
//                    }]
//                }],
//                buttons: [{
//                    id: 'saveBtn',
//                    itemId: 'saveBtn',
//                    text: 'Submit',
//                    handler: function() {
//                        this.up('form').getForm().submit();
//                    }
//                },{
//                    text: 'Cancel',
//                    handler: function() {
//                        this.up('form').getForm().reset();
//                    }
//                }],
//                submit: function() {
//                    var currentForm = this.owner.form;
//                    if (currentForm.isValid()) {
//                    }
//                }
//            });
//        });
//    },

    onResetPassword: function (options, success, response) {
        if (success) {
        	var form = this.lookupReference('form');
            if (form.isValid()) {
            	console.log(form.getValues())
	        	Ext.Ajax.request({
	                scope: this,
	                method: 'POST',
	                headers: { 'Content-Type': 'application/json' },
	                url: 'api/users/resetPassword',
	                params: JSON.stringify(form.getValues()),
	                callback: function (options, success, response) {
	                    var user, password;
	                    Ext.get('spinner').setVisible(false);
	                    if (success) {
	                    	Compassmate.app.showToast(Strings.successReset);
	                    } else {
	                        this.getView().setVisible(true);
	                        Compassmate.app.showError(response.responseText);
	                    }
	                }
	            });
	        	
	            this.closeView();
	            Compassmate.app.showToast(Strings.loginResetPassword);
            }
        } else {
            Compassmate.app.showError(response);
        }
    }

});
