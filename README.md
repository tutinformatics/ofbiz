## Ofbiz Entities

The main advantage of Ofbiz is it’s readily available collection of entities.

In this tutorial we’ll be exploring how to interact with **Entity Engine**.


#### Helpful resources

Ofbiz entities can be defined in whatever .xml file.

Definitions file must be referenced in ofbiz-component.xml file under

&lt;!-- entity resources: model(s), eca(s), group, and data definitions -->

&lt;entity-resource type="model" reader-name="main" loader="main" location="entitydef/entitymodel.xml"/>

By default, every plugin has an **entitydef **folder with entitymodel.xml, specifically to store entity definitions.

**[Ofbiz tutorial](https://cwiki.apache.org/confluence/display/OFBIZ/OFBiz%2BTutorial%2B-%2BA%2BBeginners%2BDevelopment%2BGuide#OFBizTutorial-ABeginnersDevelopmentGuide-CreatingFirstDatabaseEntity(Table))** explains entity creation and import pretty well.

The whole system is **a lot** easier to understand with the help of entity diagrams found **[HERE](https://cwiki.apache.org/confluence/display/OFBIZ/Data+Model+Diagrams).**


#### Guide

Once you have created an endpoint with your service using Camel/Jersey, you will have to process requests and query entities or use Ofbiz native services. 

You will query entities using delegator. You get it from dispatch context. You can either extract it dynamically, or define it at service initialization.

Here is how we get delegator at service initialization:

public MytService(DispatchContext context) {

   this.context = context;

   delegator = dctx.getDelegator();

}

There are two ways you can receive data for your logic: by using parameters or by sending data as a map.




#### Parameter data

If you use get request, your sent data needs to be defined in service route

(ex. api/contact/name/{name}).

public String getByParam(Exchange exchange) {


    String someData= Utils._getParamValueFromExchange_("name", exchange);


    //Etc...;

}

public static String getParamValueFromExchange(String paramName, Exchange exchange) {


       SparkMessage msg = (SparkMessage) exchange.getIn();


       Map&lt;String, String> params = msg.getRequest().params();


       String sparkParamName = ":" + paramName;

   return params.get(sparkParamName);

}

The extracted parameter will be in string format. Convert if needed.


#### Mapped data

Mapped data can be sent by POST method etc.:

public void createContact(Map&lt;String, Object> data) {

Optional&lt;GenericValue> contactList = Utils._mapToGenericValue_(delegator, "PersonData", data);

.//..

delegator.createOrStore(contactList.get());

}

public static final Converters.JSONToGenericValue _convert _= new Converters.JSONToGenericValue();

public static Optional&lt;GenericValue> mapToGenericValue(Delegator delegator, String entityName, Map&lt;String, Object> data) {


       data.put("_DELEGATOR_NAME_", delegator.getDelegatorName());


       data.put("_ENTITY_NAME_", entityName);


       try {


           return Optional._of_(_convert_.convert(JSON._from_(data)));


       } catch (ConversionException | IOException e) {


           e.printStackTrace();


       }

   return Optional._empty_();

}

When we receive mapped data from a request, we cannot simply feed it to the delegator.

We must add delegator name and entity name (if you are aiming to save the data directly).

Please remember that entity name is always capitalized, otherwise it will not be recognized.

For all available methods see delegator [docs](https://ci.apache.org/projects/ofbiz/site/javadocs/org/ofbiz/entity/Delegator.html) or even better see delegator code itself. All methods are well commented and documentation there is much more comprehensive.


<!-- Docs to Markdown version 1.0β22 -->
