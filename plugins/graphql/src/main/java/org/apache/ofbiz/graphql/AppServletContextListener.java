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
import org.apache.ofbiz.entity.model.ModelRelation;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.webapp.WebAppUtil;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.core.Context;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@WebListener
public class AppServletContextListener implements ServletContextListener {

    public static final String SCHEMA_FILE = "plugins/graphql/gql-schema/schema.graphqls";

    public static final String MODULE = AppServletContextListener.class.getName();

    @Context
    private ServletContext servletContext;

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        Delegator delegator = WebAppUtil.getDelegator(servletContext);
        LocalDispatcher dispatcher = WebAppUtil.getDispatcher(servletContext);
        writeToFile(getGraphQLSchemas(delegator, dispatcher));
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
    public String getGraphQLSchemas(Delegator delegator, LocalDispatcher dispatcher) {
        try {
            DispatchContext dpc = dispatcher.getDispatchContext();

            ///////////////////////////////// GENERATE TOP THINGY /////////////////////////////////////////////////////
            Set<Map.Entry<String, ModelEntity>> entity_entries = delegator.getModelReader().getEntityCache().entrySet();
            Map<String, ModelEntity> entitities = delegator.getModelReader().getEntityCache();
            StringBuilder schema = new StringBuilder();
            schema.append("schema {\n    query: Query\n}\n\n");

            ///////////////////////////////// GENERATE QUERIES ////////////////////////////////////////////////////////
            schema.append("type Query {\n");

            ///// GET all services
            schema.append("\tservices_ : [String]\n\n");
/*
            ///// POST service
            for (String serviceName : dispatcher.getDispatchContext().getAllServiceNames()) {
                schema.append("\tservice_").append(serviceName); //.append("(");
//                ModelService modelService = dpc.getModelService(serviceName);
//                for (Iterator<String> iter = dispatcher.getDispatchContext().getModelService(serviceName).getAllParamNames().iterator(); iter.hasNext(); ) {
//                    String param = iter.next();
//
//                    schema.append(param.replace(".", "_")).append(": ").append(getType(entitities, param, modelService.getParam(param).getType()));
//
//                    if (iter.hasNext()) {
//                        schema.append(", ");
//                    } else {
//                        schema.append(")");
//                    }
//                }

                schema.append(" : [").append(serviceName).append("]\n");
            }
*/
            for (Map.Entry<String, ModelEntity> map : new ArrayList<>(entity_entries)) {
                String key = map.getKey();


                ///// GET all entities
                schema.append("\t").append(key.toLowerCase()).append("_ : [").append(key).append("]\n");


                /////  GET entity by PK
                schema.append("\t").append(key.toLowerCase());
                generateEntityBodyWithPK(schema, map, key);


                /////  DELETE entity by PK
                schema.append("\t").append("delete").append(key.toLowerCase());
                generateEntityBodyWithPK(schema, map, key);


                ///// POST entity by body
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

                ///// POST entity body
                schema.append("\t").append("post").append(key.toLowerCase());
                generateEntityBody(schema, map, key);

                ///// PUT entity body
                schema.append("\t").append("put").append(key.toLowerCase());
                generateEntityBody(schema, map, key);

                schema.append("\n");
            }

            schema.append("}\n\n");

            ///////////////////////////////// GENERATE TYPES AND INPUTS   /////////////////////////////////////////////

            ///// for entities
            for (Map.Entry<String, ModelEntity> map : new ArrayList<>(entity_entries)) {
                schema.append("type ").append(map.getKey()).append(" {\n");

                for (String field : map.getValue().getAllFieldNames()) {
                    schema.append("\t").append(field).append(": ").append(getNewType(map.getValue().getField(field).getType())).append("\n");
                }

                for (ModelRelation toOne : map.getValue().getRelationsOneList()) {
                    schema.append("\t").append("_toOne_").append(toOne.getCombinedName().replace(" ", "")).append(": ").append(toOne.getRelEntityName()).append("\n");
                }

                for (ModelRelation toMany : map.getValue().getRelationsManyList()) {
                    schema.append("\t").append("_toMany_").append(toMany.getCombinedName().replace(" ", "")).append(": [").append(toMany.getRelEntityName()).append("]\n");
                }

                schema.append("}\n\n");

            }
/*
            ///// for input entities
            for (Map.Entry<String, ModelEntity> map : new ArrayList<>(entity_entries)) {
                schema.append("input ").append("input_").append(map.getKey()).append(" {\n");

                for (String field : map.getValue().getAllFieldNames()) {
                    schema.append("\t").append(field).append(": ").append(getNewType(map.getValue().getField(field).getType())).append("\n");
                }

//                for (ModelRelation toOne : map.getValue().getRelationsOneList()) {
//                    schema.append("\t").append("_toOne_").append(toOne.getCombinedName().replace(" ", "")).append(": ").append(toOne.getRelEntityName()).append("\n");
//                }
//
//                for (ModelRelation toMany : map.getValue().getRelationsManyList()) {
//                    schema.append("\t").append("_toMany_").append(toMany.getCombinedName().replace(" ", "")).append(": [").append(toMany.getRelEntityName()).append("]\n");
//                }

                schema.append("}\n\n");

            }

            ///// for services
            for (String serviceName : dpc.getAllServiceNames()) {
                schema.append("input ").append("input_").append(serviceName).append(" {\n");
                ModelService modelService = dpc.getModelService(serviceName);

                for (String paramName : modelService.getAllParamNames()) {
                    ModelParam param = modelService.getParam(paramName);

                    String type = param.getType();
                    try {
                        type = getInputType(entitities, paramName, type);
                    } catch (Exception ignored) {
                        type = getNewType(type);
                    }

                    schema.append("\t").append(paramName.replace(".", "_")).append(": ").append(type).append("\n");
                }

                schema.append("}\n\n");
            }


            ///// for services
            for (String serviceName : dpc.getAllServiceNames()) {
                schema.append("type ").append(serviceName).append(" {\n");
                ModelService modelService = dpc.getModelService(serviceName);

                for (String paramName : modelService.getAllParamNames()) {
                    ModelParam param = modelService.getParam(paramName);

                    String type = param.getType();
                    String output_type = param.getType();
                    try {
                        output_type = getType(entitities, paramName, type);
                        type = getInputType(entitities, paramName, type);
                    } catch (Exception ignored) {
                        output_type = getNewType(type);
                        type = getNewType(type);
                    }

                    schema.append("\t").append(paramName.replace(".", "_"));

                    if (entitities.containsKey(type)) {
                        Map.Entry<String, ModelEntity> map = new AbstractMap.SimpleEntry<>(type, entitities.get(type));
                        generateEntityBody(schema, map, type);
                    } else {
                        schema.append("(").append(paramName.replace(".", "_")).append(": ").append(type).append("): ").append(output_type).append("\n");
                    }
                }

                schema.append("}\n\n");
            }
*/
            return schema.toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void generateEntityBodyWithPK(StringBuilder schema, Map.Entry<String, ModelEntity> map, String key) {
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
    }

    private void generateEntityBody(StringBuilder schema, Map.Entry<String, ModelEntity> map, String key) {
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
    }

    @NotNull
    private String getType(Map<String, ModelEntity> entitities, String paramName, String type) {
        String capitilized = paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
        String listType = paramName.replace("List", "");
        listType = listType.substring(0, 1).toUpperCase() + listType.substring(1);
        type = getNewType(type);

        if (entitities.get(listType) != null) {
            type = "[" + listType + "]";
        }

        if (entitities.get(capitilized) != null) {
            type = capitilized;
        }
        return type;
    }

    @NotNull
    private String getInputType(Map<String, ModelEntity> entitities, String paramName, String type) {
        String capitilized = paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
        String listType = paramName.replace("List", "");
        listType = listType.substring(0, 1).toUpperCase() + listType.substring(1);
        type = getNewType(type);

        if (entitities.get(listType) != null) {
            type = "[input_" + listType + "]";
        }

        if (entitities.get(capitilized) != null) {
            type = "input_" + capitilized;
        }

        return type;
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
