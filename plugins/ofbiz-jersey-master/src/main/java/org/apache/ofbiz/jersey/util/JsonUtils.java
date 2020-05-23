package org.apache.ofbiz.jersey.util;

import org.apache.ofbiz.base.conversion.ConversionException;
import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.entity.util.ExtendedConverters;
import org.apache.ofbiz.service.GenericServiceException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JsonUtils {
    private static final ExtendedConverters.ExtendedJSONToGenericValue jsonToGenericConverter = new ExtendedConverters.ExtendedJSONToGenericValue();
    public static final ExtendedConverters.ExtendedGenericValueToJSON genericToJsonConverter = new ExtendedConverters.ExtendedGenericValueToJSON();



    public static Map<String, Object> parseJson(String json) throws Exception {
        Response.ResponseBuilder builder;

        JSON body = JSON.from(json);

        Map<String, Object> fieldMap;

        try {
            fieldMap = UtilGenerics.cast(body.toObject(Map.class));
        } catch (IOException e) {
            throw e;
        }
        // TODO: recursive converting to support multi level objects
        for (String key : fieldMap.keySet()) {
            Object obj = fieldMap.get(key);
            try {
                Map<String, Object> test = UtilGenerics.cast(JSON.from(obj).toObject(Map.class));
                if (test.containsKey("_ENTITY_NAME_")) {
                    fieldMap.put(key, jsonToGenericConverter.convert(JSON.from(obj)));
                } else {
                    fieldMap.put(key, test);
                }
            } catch (IOException | ConversionException ignored) {
                // Ignore as it just means the value isn't a separate object
            }
        }

        return fieldMap;
    }
}
