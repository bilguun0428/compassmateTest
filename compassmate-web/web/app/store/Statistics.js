 

Ext.define('Compassmate.store.Statistics', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Statistics',

    proxy: {
        type: 'rest',
        url: 'api/statistics'
    }
});
