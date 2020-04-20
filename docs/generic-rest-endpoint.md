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
    * Updates an existing entity. Fails if no entity with chosen PKs found.
* **POST** `/entityquery/{case sensitive entity name}`
    * Fetches entity with given parameters. Can set if all relations must return something for item to get returned or not. Inputfields supports everything performFind supports.
    
    ```json
    {
        "areRelationResultsMandatory": boolean (optional, default false),
        "inputFields": { (optional, default no constraints)
          "field1": "value1",
          "field2": 2
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
        "entityRelationValues" : {
            "_toOne_CurrencyUom" : {
                "areRelationResultsMandatory": false,
                "inputFields": 
                    {
                        "description": "United States Dollar"
                    },
                "fieldList": ["abbreviation", "description"],
                "entityRelationValues" : {}
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