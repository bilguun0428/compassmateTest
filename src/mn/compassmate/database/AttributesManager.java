package mn.compassmate.database;

import mn.compassmate.model.Attribute;

public class AttributesManager extends ExtendedObjectManager<Attribute> {

    public AttributesManager(DataManager dataManager) {
        super(dataManager, Attribute.class);
    }

    @Override
    public void updateCachedItem(Attribute attribute) {
        Attribute cachedAttribute = getById(attribute.getId());
        cachedAttribute.setDescription(attribute.getDescription());
        cachedAttribute.setAttribute(attribute.getAttribute());
        cachedAttribute.setExpression(attribute.getExpression());
        cachedAttribute.setType(attribute.getType());
    }

}
