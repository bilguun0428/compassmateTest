 

Ext.define('Compassmate.view.ArrayListFilter', {
    extend: 'Ext.grid.filters.filter.List',
    alias: 'grid.filter.arraylist',

    type: 'arraylist',

    constructor: function (config) {
        this.callParent([config]);
        this.filter.setFilterFn(function (item) {
            var i, property, value;
            property = item.get(this.getProperty());
            value = this.getValue();
            if (Ext.isArray(property)) {
                for (i = 0; i < property.length; i++) {
                    if (value.indexOf(property[i]) !== -1) {
                        return true;
                    }
                }
            } else if (value.indexOf(property) !== -1) {
                return true;
            }
            return false;
        });
    }
});
