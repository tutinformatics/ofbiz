**Service´i loomine.**
-
Esiteks on vaja leida services.xml faili ja luua seal service´i mis pärast loomist on nähtav ofbizil(webtools => service engine).
 
```
<service name="serviceTest" engine="java"
             location="org.apache.ofbiz.product.category.testClass" invoke="serviceFunction" auth="true">
        <description>Calls function.</description>
        <attribute name="someInput" type="String" mode="IN" optional="false"/>
</service>
```
---
**`Service parameetrid`**

- **name**: service´i nimi
- **location**: classi asukoht, kus on service´i funktsionaalsus.
- **invoke**: funktioon classist, mida käivitatakse.
---

**`Attribute parameetrid`**

- **name**: attribuuti nimetus, mida on vaja selleks, et võtta sisaldus context´ist
- **type**: attribuuti sisalduse tüüp.
- **optional**: kui attribuut on kohustuslik => false, kui mitte => true
---

`Kui service oli loodud õigesti, siis see peab olema nähtav OFBiz => Webtools => Service Engine´s.`
```
public static Map<String, Object> serviceFunction(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        ...
        return success;
}
```

---

Kui see on tehtud võib tegeleda service´i funktsionaalsusega.

- Esiteks on vaja võtta DispatchContext´ist LocalDispatcher, seda kasutatakse selleks, et kutsuda teisi service´i sünkroonselt, asünkroonselt või nagu planeeritud sündmus.

```
Map syncResults = dispatcher.runSync("service", input);

Map asyncResults = dispatcher.runAsync("service", input);

Map syncResultsIgnore = dispatcher.runSyncIgnore("service", input);
```

- Context sisaldab attribuutid, mis on saadud service´ga sellesse funktsiooni. Neid saab võtta .get(“attribute_name”) meetodiga

Näide : `String categoryId = (String) context.get("categoryId");`

Selleks, et käivitada ofbiz service sünkroonselt on vaja kasutada
```
try {

LocalDispatcher.runSync(service_name, service_context);

} catch (GenericServiceException e) {

System.err.println(e.getMessage();

}
```

**service_name** - service nimetus

**service_context** - Map, kus peavad olema kohustlikud attribuutid, mida service vajab ja kui on vaja ka teisi, mida see service saab võtta, aga ei ole kohustlikud. Seda saab vaadata  kas services.xml failist või kohe OFBiz´is kui avame seda service.

---

**`Funktsiooni lõpp.`**

Meetod peab tagastama `Map<String, Object>`. Tavaliselt tagastatakse `ServiceUtil.returnSuccess()` kui kõik oli tehtud veata ja `ServiceUtil.returnError(e.getMessage())` kui oli mingi error try/catch sees.

---
`Näide meie projektist. Siin on näha kuidas käivitatakse teist service´i läbi java classi.`
```
public static Map<String, Object> addBigBuyCategory(DispatchContext ctx, Map<String, Object> context) {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    LocalDispatcher dispatcher = ctx.getDispatcher(); //Local dispatcheri loomine
    GenericValue userLogin = (GenericValue) context.get("userLogin"); //Andmed autentimiseks
    Locale locale = (Locale) context.get("locale"); //Andmed autentimiseks
    String productCategoryTypeId = (String) context.get("productCategoryTypeId"); //Sisetatud info salvestamine
          
    // See lõik paneb contexti vajalik info
    Map<String, Object> contextCopyCategoryToCatalog = new HashMap<>(context); 
    contextCopyCategoryToCatalog.put("userLogin", userLogin); //Autentimine
    contextCopyCategoryToCatalog.put("locale", locale); //Autentimine
    contextCopyCategoryToCatalog.put("prodCatalogCategoryTypeId", "PCCT_PURCH_ALLW"); //Service´le vajalik parameeter
    contextCopyCategoryToCatalog.put("prodCatalogId", "BigBuyCatalog"); //Service´le vajalik parameeter
    contextCopyCategoryToCatalog.put("productCategoryId", productCategoryTypeId); //Service´le vajalik parameeter
        
    // käivitab service´i contextis oleva andmetega  
    try {
        dispatcher.runSync("addProductCategoryToProdCatalog", contextCopyCategoryToCatalog);
    } catch (GenericServiceException e) {
        System.err.println(e.getMessage());
    }
    
    return success;
}
```