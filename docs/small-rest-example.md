# Small REST example in Camel

https://github.com/tutinformatics/ofbiz/tree/6d27e8ba9ebcb6bb33a2560d37b9359588f791dc
(head doesn't have it any more)

Basically a bit expanded implementation of Accounting team's Camel REST API.

This is most likely not the ideal configuration and method to do it, but it works...Ish. UI team did something without Camel, so perhaps also something to take a look at.

----------------

There are 2 small modifications in [converter] class, but that's kinda dirty. The modifications basically don't add the "\_DELEGATOR_NAME\_" and "\_ENTITY_NAME\_" fields on GenericValue -> Json conversion and for Json -> GenericValue conversion you can feed those values in and it'll add them into the Json before converting to GenericValue. The conversion options are limited currently though, perhaps something to expand on if required?

Those fields are required so Ofbiz would know what entity the object is and in what database they are stored. Entity name is a string that corresponds to an entity that's defined in some .xml in the same or some other component. 

----------------

As Camel backbone was gotten from Accounting, then the plugin is still named accountingConnector. Didn't really bother with deleting the clutter.

You can define the REST methods as services in the servicedef if required for some reason.

In [route] class you map the endpoints and methods they call out, might want to look at Camel documentation on the options for that. Note that the API runs on port 4567. 

The [service] class has the methods. getInvocies() is a GET and createInvoice is a POST. POST could also get a POJO class instance in for example. If you want to call out other ofbiz services then got to use a dispatcher for it.

[converter]: https://github.com/tutinformatics/ofbiz/tree/6d27e8ba9ebcb6bb33a2560d37b9359588f791dc/framework/entity/src/main/java/org/apache/ofbiz/entity/util/Converters.java
[route]: https://github.com/tutinformatics/ofbiz/tree/6d27e8ba9ebcb6bb33a2560d37b9359588f791dc/plugins/accountingConnector/src/main/java/ee/taltech/accounting/connector/camel/routes/TemplateRoute.java
[service]: https://github.com/tutinformatics/ofbiz/tree/6d27e8ba9ebcb6bb33a2560d37b9359588f791dc/plugins/accountingConnector/src/main/java/ee/taltech/accounting/connector/camel/service/TemplateService.java