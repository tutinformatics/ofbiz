package ee.taltech.services.utils;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.Converters;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Utils {

    public static final Converters.JSONToGenericValue convert = new Converters.JSONToGenericValue();

    public static Optional<GenericValue> mapToGenericValue(Delegator delegator, String entityName, Map<String, Object> data) {
        data.put("_DELEGATOR_NAME_", delegator.getDelegatorName());
        data.put("_ENTITY_NAME_", entityName);
        try {
            return Optional.of(convert.convert(JSON.from(data)));
        } catch (ConversionException | IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
