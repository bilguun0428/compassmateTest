 

Ext.define('Compassmate.store.Notifications', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Notification',

    proxy: {
        type: 'rest',
        url: 'api/users/notifications'
    },
    sortOnLoad: true,
    sorters: {
        property: 'type',
        direction: 'ASC'
    }
});
