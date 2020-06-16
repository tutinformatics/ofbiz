**`API doc.`**
-

API (Application Program Interface) - on arvutiprogrammides alamprogrammi määratluste, protokollide ja tööriistade komplekt rakendustarkvara ehitamiseks. Üldiselt on tegemist eri tarkvarakomponentide vahelise selgelt määratletud sidevahendite kogumiga.

**Postman**

Postman on selline programm, mida kasutatakse API testimiseks. Postmanis on kõik võimalikud request meetodid (GET, POST, PUT, DELETE, ...), iga requesti jaoks on võimalik liisada ka parameetrid, authorization andmed, headers ja body.

**Põhilised request meetodid:**

- GET - kasutatakse määratud ressursi sisu taotlemiseks.
- POST - kasutatakse kasutajaandmete edastamiseks määratud ressursile.
- PUT - kasutatakse päringu sisu laadimiseks päringus täpsustatud URI-sse.
- DELETE - kustutab määratud ressursi.

Postman annab võimaluse kohe genereerida päringu paljudele programmeerimiskeeltesse (C, C#, cURL, Java, JavaScript, NodeJs, PHP, ...) ja võtta koodijuppe.


Näide (Java Unirest):

```Unirest.setTimeouts(0, 0);
HttpResponse<String> response = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/categories.json?isoCode=en")
.header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
.asString();
```

**Päringu töötlemine**

Kõik päringud annavad response JSON formaadis. JSON´is andmed on struktuuriga key:value. Meie näidel me kasutasime JsonNode ja ObjectMapper.

Näide (Java Unirest):

```import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.fasterxml.jackson.databind.ObjectMapper;

Unirest.setTimeouts(0, 0);
JsonNode products = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/products.json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
                .asJson().getBody();

ObjectMapper objectMapper = new ObjectMapper();
JsonNode productsJson = objectMapper.readTree(String.valueOf(products));

System.out.println(productsJson); // kõik andmed päringust, mida on võimalik itereerida

productsJson.get(0) // esimene produkt päringust
productsJson.get(0).get("manufacturer") // esimese produkti tootja
```

**BigBuy API**
-
Dokumentatsioon: https://api.bigbuy.eu/doc 
Esimesed sammud:
- Registreerida ennast https://www.bigbuy.eu
- My account / Control panel
- Sünkroniseerige BigBuyga ja võtta oma API token (Sandbox - testimiseks / Production - lõpptoode jaoks)


**Kasutamine**

Sandbox´is tuleb kasutada https://api.sandbox.bigbuy.eu + vajalik API. Kaasa tuleb liisada ka oma token Bearer formaadis.
Postmanis : Authorization => Type : Bearer Token, Token : BigBuy SandBox token
Iga päringu jaoks on mingi time limit (näiteks 1 päring iga 5 sec), mida saab vaadata BigBuy API dokumentatsioonis.

**Status codes:**

- 200 - success

- 404 - not found

- 415 - invalid Content-Type header

- 429 - exceeded requests limits

**Päringu tüübid:**

- GET  /rest/catalog/products.{_format} - kõik tooted (10 päringuid tunnis)
- GET  /rest/catalog/product/{id}.{_format} - üks toode kindla id´ga (1 päring iga 5 sec)



**Näide:**

Tahan võtta kõik kategooriad

https://api.sandbox.bigbuy.eu/rest/catalog/categories.{format}?isoCode=en

format - json|xml|html
isoCode - keel (es default)

https://api.sandbox.bigbuy.eu/rest/catalog/categories.json

https://api.sandbox.bigbuy.eu/rest/catalog/categories.xml 

https://api.sandbox.bigbuy.eu/rest/catalog/categories.html 

**Service**

Kuna APId võib kasutada piiratud korda tunnis, tegime service´i mis salvestab päringust saadud andmed JSON faili. 

(337 - 391 read)

https://github.com/tutinformatics/ofbiz/blob/liidestused-ee/applications/product/src/main/java/org/apache/ofbiz/product/category/BigBuyInventory.java