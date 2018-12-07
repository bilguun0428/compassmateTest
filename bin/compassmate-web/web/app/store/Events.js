 

Ext.define('Compassmate.store.Events', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Event',

    trackRemoved: false,

    proxy: {
        type: 'rest',
        url: 'api/events'
    }
});
