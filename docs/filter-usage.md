Usage is similar to performFind, as the supported actions are same.

Getting filters is from service getFilterableParameters:
```
{
    "entityName": "Invoice"
}
```
and response would be (not full response):
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
The operations list is currently not exhaustive, you can actually use any operation that is used in performFind (look at [performfind service documentation](./performfind-service.md)). You can use the "type" field to show a matching input method in frontend. id's are strings though.

Search is performed on performFilteredSearch service:
```
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
            "ignoreCase": true
        }
    ],
    "entityName": "Invoice"
}
```

The input is basically same as performFind, but you can ignore all the fldX things, but do note that max is 10 filterParameters for one field name, meaning that eleventh parameter for "partyIdFrom" would get ignored. 

And response would be:
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