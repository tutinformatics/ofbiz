package org.apache.ofbiz.graphql.utils;


import java.sql.Timestamp;

/**
 * Seek plugins/graphql/src/main/java/org/apache/ofbiz/graphql/AppServletContextListener.java for more details
 **/
public class QueryParamStringConverter {

    public static Object convert(String string, String className) {
        try {
            switch (className) {
                case "floating-point":
                    return Float.parseFloat(string);
                case "numeric":
                    return Long.parseLong(string);
                case "date":
                case "time":
                case "date-time":
                    return Timestamp.valueOf(string).getTime();
                default:
                    return string;
            }
        } catch (Exception e) {
            System.out.println(string);
            return string;
        }
    }
}

