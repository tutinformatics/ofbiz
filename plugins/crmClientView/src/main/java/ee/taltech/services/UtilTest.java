package ee.taltech.services;

import org.apache.camel.Exchange;
import org.apache.camel.component.sparkrest.SparkMessage;
import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.Converters;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UtilTest {

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
    public static String getParamValueFromExchange(String paramName, Exchange exchange) {
        SparkMessage msg = (SparkMessage) exchange.getIn();
        Map<String, String> params = msg.getRequest().params();
        String sparkParamName = ":" + paramName;
        return params.get(sparkParamName);
    }
}
