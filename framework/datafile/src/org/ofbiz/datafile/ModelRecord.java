/*
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.datafile;


import java.util.ArrayList;
import java.util.List;


/**
 *  ModelRecord
 */

public class ModelRecord {

    public static final String LIMIT_ONE = "one";
    public static final String LIMIT_MANY = "many";

    /** The name of the Record */
    public String name = "";

    /** The type-code of the Record */
    public String typeCode = "";

    /** The minimum type-code of the Record, an alternative to the single type code */
    public String tcMin = "";
    public long tcMinNum = -1;

    /** The maximum type-code of the Record, an alternative to the single type code */
    public String tcMax = "";
    public long tcMaxNum = -1;

    /** specifies whether or not the type min and max are numbers, if so does a number compare, otherwise a String compare */
    public boolean tcIsNum = true;

    /** The position of the type-code of the Record */
    public int tcPosition = -1;

    /** The length of the type-code of the Record - optional */
    public int tcLength = -1;

    /** A free form description of the Record */
    public String description = "";

    /** The name of the parent record for this record, if any */
    public String parentName = "";

    /** The number limit of records to go under the parent, may be one or many */
    public String limit = "";

    public ModelRecord parentRecord = null;
    public List childRecords = new ArrayList();

    /** List of the fields that compose this record */
    public List fields = new ArrayList();

    ModelField getModelField(String fieldName) {
        for (int i = 0; i < fields.size(); i++) {
            ModelField curField = (ModelField) fields.get(i);

            if (curField.name.equals(fieldName)) {
                return curField;
            }
        }
        return null;
    }
}

