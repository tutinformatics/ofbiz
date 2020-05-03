performFind service is a service for executing searches with additional parameters. You give in an entityName and inputFields and you get back the result.

```
{
    "inputFields": {
        "lastUpdatedStamp_fld0_op": "notEqual",
        "lastUpdatedStamp_fld0_value": 11231222323
    },
    "entityName": "Invoice"
}
```

It supports plainly giving parameters which gives results with perfect match, such as:
`"partyId": "Company"`

If you want to go more complex you have to start giving in more info. 

For every condition you have on one entity field, you must make an entityname_fld{num}, where {num} is between 0-9. Different conditions on one field must be with unique number. The range 0-9 therefore means that at most you can have 10 conditions on a single field.

For each condition you must also supply an operation which specifies what operation to make.

For each field that is a string you can also add a parameter telling it to ignore case. This seems to not work for more complex operators like "in"

Wrapping the above up in a simpler example would be:

````
"partyId_fld0_op": "equals",
"partyId_fld0_value": "cOmPaNy",
"partyId_fld0_ic": "Y",

"partyId_fld1_op": "notEquals",
"partyId_fld1_value": "Hello",

"createdStamp_fld0_op": "lessThan",
"createdStamp_fld0_value": 15151515151515
````

The different operations you can give are: 
* "equals" and "notEqual" are for equality
* "lessThan", "lessThanEqualTo", "greaterThan", "greaterThanEqualTo" do what's on the label.
* "like" is for strings, it matches with items where start of the string matches the one given in value, for example "like" "Comp" matches "Company", but not "HelloCompany"
* "not-like" or "notLike" is reverse of "like"
* "contains" is for strings, basically same as "like", but also leaves first half open, so that means "contains" "omp" matches "Company"
* "not-contains" or "notContains" is reverse of "contains", both variations work.
* "empty" matches if value is null, this field doesn't seem to require a _fldX_value parameter with it
* "in" is for checking if a value is in a list, so for example "in" ["Company", "TaxMan", "foo"].
* "not-in" (no second form here) is reverse of "in" 

So the above can be used in various ways to make your dream query parameter list, almost, as max conditions for one field is 10 (_fld0 to _fld9)