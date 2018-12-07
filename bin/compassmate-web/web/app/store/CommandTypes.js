 

Ext.define('Compassmate.store.CommandTypes', {
    extend: 'Ext.data.Store',
    fields: ['type', 'name'],

    proxy: {
        type: 'rest',
        url: 'api/commandtypes',
        reader: {
            type: 'json',
            getData: function (data) {
                Ext.each(data, function (entry) {
                    var nameKey, name;
                    entry.name = entry.type;
                    if (typeof entry.type !== 'undefined') {
                        nameKey = 'command' + entry.type.charAt(0).toUpperCase() + entry.type.slice(1);
                        name = Strings[nameKey];
                        if (typeof name !== 'undefined') {
                            entry.name = name;
                        }
                    }
                });
                return data;
            }
        },
        listeners: {
            'exception': function (proxy, response) {
                Compassmate.app.showError(response);
            }
        }
    }
});
