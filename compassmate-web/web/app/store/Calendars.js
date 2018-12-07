 

Ext.define('Compassmate.store.Calendars', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Calendar',

    proxy: {
        type: 'rest',
        url: 'api/calendars',
        writer: {
            writeAllFields: true
        }
    }
});
