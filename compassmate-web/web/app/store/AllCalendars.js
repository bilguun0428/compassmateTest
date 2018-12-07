 

Ext.define('Compassmate.store.AllCalendars', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Calendar',

    proxy: {
        type: 'rest',
        url: 'api/calendars',
        extraParams: {
            all: true
        }
    }
});
