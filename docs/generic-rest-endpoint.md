Generic REST endpoint is at `:8443/api/generic/`

Not all exceptions are nicely handled currently.

Currently the following /v1 endpoints (meaning `/api/generic/v1/*`) are implemented:
* **GET** `/structure/entities/{case sensitive entity name}`
    * Returns some general info about queried entity.
* **GET** `/entities`
    * Returns list of all entity names
* **GET** `/entities/{case sensitive entity name}?field1=value&field2=value`
    * Returns all entities (with no related / sub entities) that match the query parameters. Custom parameter "_query" accepts an int to say how many subleves you want. Currently limited to 1 as this makes request grow in size super fast. Related single items are with prefix "_Related\_", related lists of items are with prefix "_RelatedList\_". It being list or single is taken from entity declaration.
* **DELETE** `/entities/{case sensitive entity name}?field1=val1&field2=val2`
    * Deletes entity matching those fields if and only if there is just one such entity. For more extensive deletes it would probably make more sense to use a service.
* **POST** `/entities/{case sensitive entity name}`
    * Make a new entity, doesn't support subobjects or adding relations. For those use existing or own services or assist with implementing them on a generic level.
    Fails if you try to add something with lacking PK fields or PKs that are in conflict with an existing entity.
* **PUT** `/entities/{case sensitive entity name}`
    * Updates an existing entity. Updates the given fields to given values, doesn't change other fields. Fails if no entity with chosen PKs found. PKs are taken from entity structure.
* **POST** `/entityquery/{case sensitive entity name}`
    * Fetches entity with given parameters. Can set if all relations must return something for item to get returned or not. Inputfields supports everything performFind supports.
    * in "field3_fld0_op" the "like" is one example. Check [performFind docs for supported operations](./performfind-service.md)
    * For ranges you have to use two inputFields, first with fld0 and second with fld1 like in example
    * Times can be entered in milliseconds or in strings edible for java.sql.Timestamp in format "yyyy-[m]m-[d]d hh:mm:ss[.f...]"
    
    ```json
    {
        "areRelationResultsMandatory": boolean (optional, default false),
        "inputFields": { (optional, default no constraints)
          "field": ["any", "value", "match", "from", "list"],
          "field1": "value1",
          "field2": 2,
          "field3_fld0_op": "like",
          "field3_fld0_value": "likeparametervalue",
          "numericfield4_fld0_op": "greaterThan",
          "numericfield4_fld0_value": 12,
          "numericfield4_fld1_op": "lessThanEqualTo",
          "numericfield4_fld1_value": "2019-12-01 20:09:01",
          "stringfield_fld0_op": "in",
          "stringfield_fld0_value": ["str1", "str2"]
        },
        "fieldList": ["List", "of", "returned", "fields"] (optional, default all fields),
        "entityRelations": { (optional, default no relations, only given relations are returned)
          "_toOne_RelationName": {
            this same object recursively
          }
        }
    }
    ```
  
    ```json
    {
        "areRelationResultsMandatory": true,
        "inputFields": 
            {
                "partyIdFrom": "AcctBigSupplier"
            },
        "fieldList": ["partyIdFrom", "invoiceTypeId", "dueDate", "description"],
        "entityRelations" : {
            "_toOne_CurrencyUom" : {
                "areRelationResultsMandatory": false,
                "inputFields": 
                    {
                        "description": "United States Dollar"
                    },
                "fieldList": ["abbreviation", "description"],
                "entityRelations" : {}
            }
        }
    }
    ```
* **GET** `/services`
    * Gets list of all service names
* **GET** `/services/{case sensitive service name}`
    * Returns list of service parameters
* **POST** `/services/{case sensitive service name}`
    * Calls service. Some conversions may fail (json numeric to timestamp, for example). POST object must be a JSON object with key:values that are required by the service. If you want to add an entity, then the entity object must have key "_ENTITY_NAME\_" with value of case-sensitive entity name in it.
    Example POST to service performFind:
```json 
{
    "entityName": "Invoice",
    "noConditionFind": "Y",
    "inputFields": 
        {
        }
}
```

If there are any errors or troubles either let me know, fix them, or suffer. None of them are mutually exclusive.