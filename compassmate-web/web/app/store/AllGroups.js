 

Ext.define('Compassmate.store.AllGroups', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Group',

    proxy: {
        type: 'rest',
        url: 'api/groups',
        extraParams: {
            all: true
        }
    }
});
