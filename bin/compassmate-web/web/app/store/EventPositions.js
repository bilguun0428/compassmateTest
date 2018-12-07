 

Ext.define('Compassmate.store.EventPositions', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Position',

    trackRemoved: false,

    proxy: {
        type: 'rest',
        url: 'api/positions',
        headers: {
            'Accept': 'application/json'
        }
    }
});
