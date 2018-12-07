
Ext.define('Compassmate.view.dialog.CalendarController', {
    extend: 'Compassmate.view.dialog.BaseEditController',
    alias: 'controller.calendar',

    onFileChange: function (fileField) {
        var reader;
        if (fileField.fileInputEl.dom.files.length > 0) {
            reader = new FileReader();
            reader.onload = function (event) {
                fileField.up('window').lookupReference('dataField').setValue(
                    event.target.result.substr(event.target.result.indexOf(',') + 1));
            };
            reader.onerror = function (event) {
                Compassmate.app.showError(event.target.error);
            };
            reader.readAsDataURL(fileField.fileInputEl.dom.files[0]);
        }
    }
});
