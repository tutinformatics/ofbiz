Usage is similar to performFind, as the supported actions are same.

These are implemented as services called "getFilterableParameters" and "performFilteredSearch". As they're services, you can access them with generic Jersey endpoint for services, POST body for the service would be what you see here.

Getting filters is from service getFilterableParameters:
```
{
    "entityName": "Invoice"
}
```
and response would be (not full response, may be out of date):
```
    "responseMessage": "success",
    "filterings": {
        "partyIdFrom": {
            "operations": [
                "equals",
                "notEqual"
            ],
            "fieldName": "partyIdFrom",
            "type": "id"
        },
        "lastUpdatedStamp": {
            "operations": [
                "equals",
                "greaterThan",
                "greaterThanEqualTo",
                "lessThan",
                "lessThanEqualTo",
                "notEqual"
            ],
            "fieldName": "lastUpdatedStamp",
            "type": "date-time"
        },
        "roleTypeId": {
            "operations": [
                "equals",
                "notEqual"
            ],
            "fieldName": "roleTypeId",
            "type": "id"
        },
        "recurrenceInfoId": {
            "operations": [
                "equals",
                "notEqual"
            ],
            "fieldName": "recurrenceInfoId",
            "type": "id"
        }
    }
}
```
The operations list may not be exhaustive, you can actually use any operation that is used in performFind (look at [performfind service documentation](./performfind-service.md)). 

You can use the "type" field to show a matching input method in frontend. type: id is string. Or for example for "date" you could put a calendar for input. You can make a dropdown for all of the operations and if you would like to put more readable strings, just use the given names (like "lessThanEqualTo") as select element value and based on that build the request for backend.

The operations list is (kinda) based on the "type" field, so in case you don't want to show a certain operation for certain types, you can just blacklist it, for example if type = id, then don't show "notEqual"

Search is performed on performFilteredSearch service:
Structure:
```
{
    "filterParameters": [  // list of similar objects
        {
            "fieldName": "string",  // an existing field name in entity
            "operation": "string",  // an operation from performFind operations list
            "value": val,   // a value, can be string or numeric or timestamp in milliseconds or string form accepted by performFind
            "ignoreCase": bool, // optional, if true, it ignores case for the given operation and value string
            "group": "string" // optional, if given, puts this parameter into group of given string, see performFind docs on how it works
        }
    ],
    "entityName": "stringForEntityName"
}
```
And an example
```json
{
    "filterParameters": [
        {
            "fieldName": "lastUpdatedStamp",
            "operation": "equals",
            "value": 1588007772625
        },
        {
            "fieldName": "lastUpdatedStamp",
            "operation": "equals",
            "value": 1588007772625
        },
        {
            "fieldName": "partyIdFrom",
            "operation": "equals",
            "value": "company",
            "ignoreCase": true,
            "group": "a"
        },
        {
            "fieldName": "partyId",
            "operation": "equals",
            "value": "company",
            "ignoreCase": true,
            "group": "b"
        }
    ],
    "entityName": "Invoice"
}
```

The input is basically same as performFind, but you can ignore all the fldX things, but do note that max is 10 filterParameters for one field name, meaning that eleventh parameter for "partyIdFrom" would get ignored.

This also supports groups! Also you can (hopefully) ignore the performFind limitation / bug brought out in end of performFind docs, so tl;dr order for filterParameters doesn't matter. 

And response would be in style of:
```
{
    "result": [
        {
            "partyIdFrom": "Company",
            "lastUpdatedStamp": 1588007772625,
            "roleTypeId": null,
            "recurrenceInfoId": null,
            "createdTxStamp": 1588007770512,
            "invoiceTypeId": "SALES_INVOICE",
            "dueDate": 1148550387122,
            "createdStamp": 1588007772625,
            "description": "This is the first invoice number to AcctBuyer",
            "lastUpdatedTxStamp": 1588007770512,
            "billingAccountId": null,
            "invoiceDate": 1145958387122,
            "contactMechId": null,
            "currencyUomId": "USD",
            "statusId": "INVOICE_IN_PROCESS",
            "paidDate": null,
            "referenceNumber": null,
            "invoiceId": "demo10000",
            "invoiceMessage": null,
            "partyId": "AcctBuyer"
        }
    ]
}
```