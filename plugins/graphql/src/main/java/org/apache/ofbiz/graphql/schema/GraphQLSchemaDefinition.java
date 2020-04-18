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
package org.apache.ofbiz.graphql.schema;

import graphql.language.ScalarTypeDefinition;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.ofbiz.base.util.FileUtil;
import org.apache.ofbiz.graphql.AppServletContextListener;
import org.apache.ofbiz.graphql.fetcher.EntityDataFetcher;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class GraphQLSchemaDefinition {


    /**
     * Creates a new GraphQLSchema using SDL
     *
     * @return
     */
    public GraphQLSchema newSDLSchema() {
        SchemaParser schemaParser = new SchemaParser();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        Reader cdpSchemaReader = getSchemaReader(AppServletContextListener.SCHEMA_FILE);
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        typeRegistry.merge(schemaParser.parse(cdpSchemaReader));
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private static Reader getSchemaReader(String resourceUrl) {
        File schemaFile = FileUtil.getFile(resourceUrl);
        try {
            return new InputStreamReader(new FileInputStream(schemaFile), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Builds Runtime Wiring for the schema types defined
     *
     * @return
     */
    private RuntimeWiring buildRuntimeWiring() {
        RuntimeWiring.Builder build = RuntimeWiring.newRuntimeWiring();
        build.type(newTypeWiring("Query").defaultDataFetcher(new EntityDataFetcher()));
        return build.build();
    }

}
