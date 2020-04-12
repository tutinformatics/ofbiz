/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.graphql;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.model.ModelEntity;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.webapp.WebAppUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@WebListener
public class AppServletContextListener implements ServletContextListener {

    public static final String SCHEMA_FILE = "plugins/graphql/gql-schema/schema.graphqls";

    public static final String MODULE = AppServletContextListener.class.getName();

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        Delegator delegator = WebAppUtil.getDelegator(servletContext);
        writeToFile(getGraphQLSchemas(delegator));
        LocalDispatcher dispatcher = WebAppUtil.getDispatcher(servletContext);
        Debug.logInfo("GraphQL Context initialized, delegator " + delegator + ", dispatcher", MODULE);
        servletContext.setAttribute("delegator", delegator);
        servletContext.setAttribute("dispatcher", dispatcher);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Debug.logInfo("GraphQL Context destroyed, removing delegator and dispatcher ", MODULE);
        context.removeAttribute("delegator");
        context.removeAttribute("dispatcher");
    }

    /**
     * Generate GraphQL Schema
     *
     * @return Schema
     */
    public String getGraphQLSchemas(Delegator delegator) {
        try {
            ///////////////////////////////// GENERATE TOP THINGY /////////////////////////////////////////////////////
            Set<Map.Entry<String, ModelEntity>> entries = delegator.getModelReader().getEntityCache().entrySet();
            StringBuilder schema = new StringBuilder();
            schema.append("schema {\n    query: Query\n}\n\n");

            ///////////////////////////////// GENERATE QUERIES ////////////////////////////////////////////////////////
            schema.append("type Query {\n");

            for (Map.Entry<String, ModelEntity> map : new ArrayList<>(entries)) {
                String key = map.getKey();


                ///// GET all
                schema.append("\t").append(key.toLowerCase()).append("_ : [").append(key).append("]\n");


                /////  GET by PK
                schema.append("\t").append(key.toLowerCase());
                if (map.getValue().getPkFieldNames().size() != 0) {
                    schema.append("(");
                    for (Iterator<String> iter = map.getValue().getPkFieldNames().iterator(); iter.hasNext(); ) {
                        String pk = iter.next();
                        schema.append(pk).append(" : ").append(getNewType(map.getValue().getField(pk).getType()));
                        if (iter.hasNext()) {
                            schema.append(", ");
                        } else {
                            schema.append(")");
                        }
                    }
                }
                schema.append(" : ").append(key).append("\n");


                /////  DELETE by PK
                schema.append("\t").append("delete").append(key.toLowerCase());
                if (map.getValue().getPkFieldNames().size() != 0) {
                    schema.append("(");
                    for (Iterator<String> iter = map.getValue().getPkFieldNames().iterator(); iter.hasNext(); ) {
                        String pk = iter.next();
                        schema.append(pk).append(" : ").append(getNewType(map.getValue().getField(pk).getType()));
                        if (iter.hasNext()) {
                            schema.append(", ");
                        } else {
                            schema.append(")");
                        }
                    }
                }
                schema.append(" : ").append(key).append("\n");


                ///// POST body
                if (map.getValue().getPkFieldNames().size() == 1) {
                    schema.append("\t").append("post").append(key.toLowerCase()).append("_");
                    if (map.getValue().getAllFieldNames().size() != 0) {
                        schema.append("(");
                        List<String> vals = map.getValue().getAllFieldNames();
                        vals.remove(map.getValue().getPkFieldNames().get(0));
                        for (Iterator<String> iter = vals.iterator(); iter.hasNext(); ) {
                            String k = iter.next();
                            schema.append(k).append(" : ").append(getNewType(map.getValue().getField(k).getType()));
                            if (iter.hasNext()) {
                                schema.append(", ");
                            } else {
                                schema.append(")");
                            }

                        }
                    }
                    schema.append(" : ").append(key).append("\n");
                }

                ///// POST body
                schema.append("\t").append("post").append(key.toLowerCase());
                if (map.getValue().getAllFieldNames().size() != 0) {
                    schema.append("(");
                    for (Iterator<String> iter = map.getValue().getAllFieldNames().iterator(); iter.hasNext(); ) {
                        String k = iter.next();
                        schema.append(k).append(" : ").append(getNewType(map.getValue().getField(k).getType()));
                        if (iter.hasNext()) {
                            schema.append(", ");
                        } else {
                            schema.append(")");
                        }
                    }
                }
                schema.append(" : ").append(key).append("\n");

                ///// PUT body
                schema.append("\t").append("put").append(key.toLowerCase());
                if (map.getValue().getAllFieldNames().size() != 0) {
                    schema.append("(");
                    for (Iterator<String> iter = map.getValue().getAllFieldNames().iterator(); iter.hasNext(); ) {
                        String k = iter.next();
                        schema.append(k).append(" : ").append(getNewType(map.getValue().getField(k).getType()));
                        if (iter.hasNext()) {
                            schema.append(", ");
                        } else {
                            schema.append(")");
                        }
                    }
                }
                schema.append(" : ").append(key).append("\n");

                schema.append("\n");
            }

            schema.append("}\n\n");

            ///////////////////////////////// GENERATE TYPES //////////////////////////////////////////////////////////
            for (Map.Entry<String, ModelEntity> map : new ArrayList<>(entries)) {
                schema.append("type ").append(map.getKey()).append(" {\n");

                for (String field : map.getValue().getAllFieldNames()) {
                    schema.append("\t").append(field).append(": ").append(getNewType(map.getValue().getField(field).getType())).append("\n");
                }
                schema.append("}\n\n");

            }
            return schema.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * @param type: ofbiz type
     * @return Int, Float, String or Boolean
     **/
    private static String getNewType(String type) {
        if (new ArrayList<>(Arrays.asList()).contains(type)) {
            return "Int";
        } else if (new ArrayList<>(Arrays.asList()).contains(type)) {
            return "Boolean";
        } else if (new ArrayList<>(Arrays.asList("floating-point")).contains(type)) {
            return "Float";
        } else if (new ArrayList<>(Arrays.asList("numeric", "date", "time", "date-time")).contains(type)) {
            return "Long";
        } else {
            return "String";
        }
    }

    private static void writeToFile(String content) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(SCHEMA_FILE))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
