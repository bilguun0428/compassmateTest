 

Ext.define('Compassmate.store.AllNotifications', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Notification',

    proxy: {
        type: 'rest',
        url: 'api/users/notifications',
        extraParams: {
            all: true
        }
    },
    sortOnLoad: true,
    sorters: {
        property: 'type',
        direction: 'ASC'
    }
});
