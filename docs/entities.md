**Ofbiz Entities** (this guide is quite unnecessary)    
  
  
The main advantage of Ofbiz is it’s readily available collection of entities.
We’ll be exploring how to interact with Entity Engine.

Ofbiz entities can be defined in whatever .xml file.
Definitions file must be referenced in ofbiz-component.xml file under
<!-- entity resources: model(s), eca(s), group, and data definitions -->
<entity-resource type="model" reader-name="main" loader="main" location="entitydef/entitymodel.xml"/>
By default, every plugin has an entitydef folder with entitymodel.xml, specifically to store entity definitions.
Ofbiz tutorial explains entity creation and import pretty well.
The whole system is a lot easier to understand with the help of entity diagrams found HERE:  
  (https://cwiki.apache.org/confluence/display/OFBIZ/Data+Model+Diagrams).  
<pre>

</pre>

**Guide**

Once you have created an endpoint with your service using Camel/Jersey, you will have to process requests and query entities or use Ofbiz native services. 

You will query entities using delegator. You get it from dispatch context. You can either extract it dynamically, or define it at service initialization.
Here is how we get delegator at service initialization:  
<pre>
public MytService(DispatchContext context) {  
    this.context = context;  
    delegator = dctx.getDelegator();  
}  
</pre>
There are two ways you can receive data for your logic: by using parameters or by sending data as a map.

Parameter data
If you use get request, your sent data needs to be defined in service route
(ex. api/contact/name/{name}).  
<pre>

public String getByParam(Exchange exchange) {
String someData= Utils.getParamValueFromExchange("name", exchange);
//Etc...;
}

public static String getParamValueFromExchange(String paramName, Exchange exchange) {
   SparkMessage msg = (SparkMessage) exchange.getIn();
   Map<String, String> params = msg.getRequest().params();
   String sparkParamName = ":" + paramName;
   return params.get(sparkParamName);
}
</pre>
The extracted parameter will be in string format. Convert if needed.
Mapped data
Mapped data can be sent by POST method etc.:
<pre>
public void createContact(Map<String, Object> data) {
Optional<GenericValue> contactList = Utils.mapToGenericValue(delegator, "PersonData", data);
.//..
delegator.createOrStore(contactList.get());
}

public static final Converters.JSONToGenericValue convert = new Converters.JSONToGenericValue();


public static Optional<GenericValue> mapToGenericValue(Delegator delegator, String entityName, Map<String, Object> data) {
   data.put("_DELEGATOR_NAME_", delegator.getDelegatorName());
   data.put("_ENTITY_NAME_", entityName);
   try {
       return Optional.of(convert.convert(JSON.from(data)));
   } catch (ConversionException | IOException e) {
       e.printStackTrace();
   }
   return Optional.empty();
}
</pre>
When we receive mapped data from a request, we cannot simply feed it to the delegator.
We must add delegator name and entity name (if you are aiming to save the data directly).
Please remember that entity name is always capitalized, otherwise it will not be recognized.

For all available methods see delegator docs or even better see delegator code itself. All methods are well commented and documentation there is much more comprehensive.
