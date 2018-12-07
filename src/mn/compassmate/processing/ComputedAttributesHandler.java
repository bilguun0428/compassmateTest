package mn.compassmate.processing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import mn.compassmate.BaseDataHandler;
import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Attribute;
import mn.compassmate.model.Device;
import mn.compassmate.model.Position;

public class ComputedAttributesHandler extends BaseDataHandler {

    private JexlEngine engine;

    private boolean mapDeviceAttributes;

    public ComputedAttributesHandler() {
        engine = new JexlEngine();
        engine.setStrict(true);
        if (Context.getConfig() != null) {
            mapDeviceAttributes = Context.getConfig().getBoolean("processing.computedAttributes.deviceAttributes");
        }
    }

    private MapContext prepareContext(Position position) {
        MapContext result = new MapContext();
        if (mapDeviceAttributes) {
            Device device = Context.getIdentityManager().getById(position.getDeviceId());
            if (device != null) {
                for (Object key : device.getAttributes().keySet()) {
                    result.set((String) key, device.getAttributes().get(key));
                }
            }
        }
        Set<Method> methods = new HashSet<>(Arrays.asList(position.getClass().getMethods()));
        methods.removeAll(Arrays.asList(Object.class.getMethods()));
        for (Method method : methods) {
            if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
                String name = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);

                try {
                    if (!method.getReturnType().equals(Map.class)) {
                        result.set(name, method.invoke(position));
                    } else {
                        for (Object key : ((Map) method.invoke(position)).keySet()) {
                            result.set((String) key, ((Map) method.invoke(position)).get(key));
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException error) {
                    Log.warning(error);
                }
            }
        }
        return result;
    }

    public Object computeAttribute(Attribute attribute, Position position) throws JexlException {
        return engine.createExpression(attribute.getExpression()).evaluate(prepareContext(position));
    }

    @Override
    protected Position handlePosition(Position position) {
        Collection<Attribute> attributes = Context.getAttributesManager().getItems(
                Context.getAttributesManager().getAllDeviceItems(position.getDeviceId()));
        for (Attribute attribute : attributes) {
            if (attribute.getAttribute() != null) {
                Object result = null;
                try {
                    result = computeAttribute(attribute, position);
                } catch (JexlException error) {
                    Log.warning(error);
                }
                if (result != null) {
                    try {
                        switch (attribute.getType()) {
                            case "number":
                                position.getAttributes().put(attribute.getAttribute(), (Number) result);
                                break;
                            case "boolean":
                                position.getAttributes().put(attribute.getAttribute(), (Boolean) result);
                                break;
                            default:
                                position.getAttributes().put(attribute.getAttribute(), result.toString());
                        }
                    } catch (ClassCastException error) {
                        Log.warning(error);
                    }
                }
            }
        }
        return position;
    }

}
