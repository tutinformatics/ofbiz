Generic REST endpoint is at :8443/api/

Currently the following /v1 endpoints are implemented:
* GET /entities
    * Returns list of all entity names
* GET /entities/{case sensitive entity name}?field1=value&field2=value
    * Returns all entities (with no related / sub entities) that match the query parameters. Don't add any to get all.
* PUT /entities/{case sensitive entity name}
    * Make a new entity, doesn't support subobjects or adding relations. For those use existing or own services or assist with implementing them on a generic level.
    Fails if you try to add something with lacking PK fields or PKs that are in conflict with an existing entity.
* POST /entities/{case sensitive entity name}
    * Updates an existing entity. Fails if no entity with chosen PKs found.
* GET /services
    * Gets list of all service names
* GET /services/{case sensitive service name}
    * Returns list of service parameters
* POST /services/{case sensitive service name}
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