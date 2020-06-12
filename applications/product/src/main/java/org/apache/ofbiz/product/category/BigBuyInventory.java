package org.apache.ofbiz.product.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.apache.camel.model.dataformat.JsonLibrary.Jackson;

@SuppressWarnings("Duplicates")
public class BigBuyInventory {
    public static Map<String, Object> addBigBuyCategory(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String productCategoryTypeId = (String) context.get("productCategoryTypeId");

        //category to catalog context
        Map<String, Object> contextCopyCategoryToCatalog = new HashMap<>(context);
        contextCopyCategoryToCatalog.put("userLogin", userLogin);
        contextCopyCategoryToCatalog.put("locale", locale);
        contextCopyCategoryToCatalog.put("prodCatalogCategoryTypeId", "PCCT_PURCH_ALLW");
        contextCopyCategoryToCatalog.put("prodCatalogId", "BigBuyCatalog");
        contextCopyCategoryToCatalog.put("productCategoryId", productCategoryTypeId);

        Map<String, Object> contextSupplierToCategory = new HashMap<>(context);
        contextSupplierToCategory.put("userLogin", userLogin);
        contextSupplierToCategory.put("locale", locale);
        contextSupplierToCategory.put("partyId", "BigBuy");
        contextSupplierToCategory.put("productCategoryId", productCategoryTypeId);
        contextSupplierToCategory.put("roleTypeId", "SUPPLIER");


        //  Adding Category
        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode response = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/category/" + productCategoryTypeId + ".json?isoCode=en")
                .header("Authorization", "Bearer YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA")
                .asJson().getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(String.valueOf(response));
        if (jsonNode.get("name") != null) {
            context.put("categoryName", String.valueOf(jsonNode.get("name")).replace("\"", ""));
        }
        context.put("productCategoryId", productCategoryTypeId);
        context.put("productCategoryTypeId", "CATALOG_CATEGORY");

        try {
            dispatcher.runSync("createProductCategory", context);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //  Adding supplier to category
        try {
            dispatcher.runSync("addPartyToCategory", contextSupplierToCategory);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //  Adding Category to Catalog
        try {
            dispatcher.runSync("addProductCategoryToProdCatalog", contextCopyCategoryToCatalog);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        return success;
    }

    public static Map<String, Object> addBigBuyCatalogAndSupplier(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        //supplier name
        String partyId = "BigBuy";
        //catalog name
        String catalogNameAndId = "BigBuyCatalog";

        //  Adding Catalog
        Map<String, Object> contextCopyCatalog = new HashMap<>(context);
        contextCopyCatalog.put("userLogin", userLogin);
        contextCopyCatalog.put("locale", locale);
        contextCopyCatalog.put("catalogName", catalogNameAndId);
        contextCopyCatalog.put("prodCatalogId", catalogNameAndId);

        try {
            dispatcher.runSync("createProdCatalog", contextCopyCatalog);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //supplier context
        Map<String, Object> supplierContext = new HashMap<>(context);
        supplierContext.put("userLogin", userLogin);
        supplierContext.put("locale", locale);
        supplierContext.put("groupName", partyId);
        supplierContext.put("partyId", partyId);
        supplierContext.put("partyTypeId", "PARTY_GROUP");
        supplierContext.put("preferredCurrencyUomId", "EUR");

        //supplier role context
        Map<String, Object> supplierRoleContext = new HashMap<>(context);
        supplierRoleContext.put("userLogin", userLogin);
        supplierRoleContext.put("locale", locale);
        supplierRoleContext.put("partyId", partyId);
        supplierRoleContext.put("roleTypeId", "SUPPLIER");

        try {
            dispatcher.runSync("createPartyGroup", supplierContext);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        try {
            dispatcher.runSync("createPartyRole", supplierRoleContext);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        //supplier to category context
        Map<String, Object> supplierToCatalogContext = new HashMap<>(context);
        supplierToCatalogContext.put("userLogin", userLogin);
        supplierToCatalogContext.put("locale", locale);
        supplierToCatalogContext.put("partyId", partyId);
        supplierToCatalogContext.put("prodCatalogId", catalogNameAndId);
        supplierToCatalogContext.put("roleTypeId", "SUPPLIER");

        try {
            dispatcher.runSync("addProdCatalogToParty", supplierToCatalogContext);
        } catch (GenericServiceException e) {
            System.err.println(e.getMessage());
        }

        return success;
    }

    public static Map<String, Object> addProducts(DispatchContext ctx, Map<String, Object> context) throws UnirestException, IOException {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String,Object> success = ServiceUtil.returnSuccess();

        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Locale locale = (Locale)context.get("locale");
        String categoryId = (String) context.get("categoryId");

        //1 token: YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA
        //2 token: MGJlMGFlYWJiZjNlOTNkZTk1ZmQyMTA1MjI3NzljMWQwNjFkZTAyNjdhNDA5Y2ExNmJkN2MzOWQ5NzE3YjMyNw

        String token = "YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA";

        ObjectMapper objectMapper = new ObjectMapper();

        String productsJson = "applications/product/BigBuyData/products.json";
        com.fasterxml.jackson.databind.JsonNode productsList = objectMapper.readTree(new FileReader(productsJson));

        //add product context

        Map<String, Map<String, String>> productAttributes = new HashMap<>();

        for (JsonNode node : productsList) {
            String category  = String.valueOf(node.get("category"));
            String id  = String.valueOf(node.get("id"));
            if (category.equals(categoryId)) {
                Map<String, String> values = new HashMap<>();

                String price  = String.valueOf(node.get("inShopsPrice"));
                String sku = String.valueOf(node.get("sku"));
                String ean = String.valueOf(node.get("ean13"));
                String weight = String.valueOf(node.get("weight"));
                String height = String.valueOf(node.get("height"));
                String width = String.valueOf(node.get("width"));
                String depth = String.valueOf(node.get("depth"));
                String brandId  = String.valueOf(node.get("manufacturer"));

                values.put("price", price);
                values.put("sku", sku);
                values.put("ean", ean);
                values.put("weight", weight);
                values.put("height", height);
                values.put("width", width);
                values.put("depth", depth);
                values.put("brandId", brandId);

                productAttributes.put(id, values);
            }
        }

        String productsInformationJson = "applications/product/BigBuyData/productsinformation.json";
        com.fasterxml.jackson.databind.JsonNode productsInformationList = objectMapper.readTree(new FileReader(productsInformationJson));


        String filename = "applications/product/BigBuyData/manufacturers.json";
        com.fasterxml.jackson.databind.JsonNode brands = objectMapper.readTree(new FileReader(filename));

        Set<String> idList = productAttributes.keySet();

        if (idList.size() != 0) {
            for (JsonNode node : productsInformationList) {
                String id = String.valueOf(node.get("id"));
                if (idList.contains(id)) {
                    //createProduct
                    Map<String, Object> productContext = new HashMap<>(context);
                    double height = Double.parseDouble(productAttributes.get(id).get("height"));
                    double weight = Double.parseDouble(productAttributes.get(id).get("weight"));
                    double width = Double.parseDouble(productAttributes.get(id).get("width"));
                    double depth = Double.parseDouble(productAttributes.get(id).get("depth"));

                    BigDecimal heightBD = BigDecimal.valueOf(height);
                    BigDecimal weightBD = BigDecimal.valueOf(weight);
                    BigDecimal widthBD = BigDecimal.valueOf(width);
                    BigDecimal depthBD = BigDecimal.valueOf(depth);

                    productContext.put("userLogin", userLogin);
                    productContext.put("locale", locale);
                    String name = String.valueOf(node.get("name")).replace("\"", "");
                    productContext.put("productName", name);
                    productContext.put("internalName", name);
                    productContext.put("productId", id);
                    productContext.put("productTypeId", "GOOD");
                    productContext.put("requirementMethodEnumId", "PRODRQM_DS");
                    productContext.put("productHeight", heightBD);
                    productContext.put("productWeight", weightBD);
                    productContext.put("productWidth", widthBD);
                    productContext.put("productDepth", depthBD);
                    productContext.put("primaryProductCategoryId", categoryId);

                    for (JsonNode brand : brands) {
                        String brandIdLoop = String.valueOf(brand.get("id"));
                        if (productAttributes.get(id).get("brandId").equals(brandIdLoop)) {
                            String brandName = String.valueOf(brand.get("name")).replace("\"", "");
                            productContext.put("brandName", brandName);
                        }
                    }

                    try {
                        dispatcher.runSync("createProduct", productContext);
                    } catch (GenericServiceException e) {
                        System.err.println(e.getMessage());
                    }

                    //add sku
                    String sku = String.valueOf(productAttributes.get(id).get("sku")).replace("\"", "");
                    Map<String, Object> skuContext = new HashMap<>(context);
                    skuContext.put("userLogin", userLogin);
                    skuContext.put("locale", locale);
                    skuContext.put("goodIdentificationTypeId", "SKU");
                    skuContext.put("idValue", sku);
                    skuContext.put("productId", id);

                    try {
                        dispatcher.runSync("createGoodIdentification", skuContext);
                    } catch (GenericServiceException e) {
                        System.err.println(e.getMessage());
                    }

                    //add ean
                    String ean = String.valueOf(productAttributes.get(id).get("ean")).replace("\"", "");
                    Map<String, Object> eanContext = new HashMap<>(context);
                    eanContext.put("userLogin", userLogin);
                    eanContext.put("locale", locale);
                    eanContext.put("goodIdentificationTypeId", "EAN");
                    eanContext.put("idValue", ean);
                    eanContext.put("productId", id);

                    try {
                        dispatcher.runSync("createGoodIdentification", eanContext);
                    } catch (GenericServiceException e) {
                        System.err.println(e.getMessage());
                    }

                    //add a price
                    double priceDB = Double.parseDouble(productAttributes.get(id).get("price"));
                    BigDecimal priceBD = BigDecimal.valueOf(priceDB);
                    Map<String, Object> priceContext = new HashMap<>(context);
                    priceContext.put("userLogin", userLogin);
                    priceContext.put("locale", locale);
                    priceContext.put("cost", priceBD);
                    priceContext.put("productId", id);
                    priceContext.put("costUomId", "EUR");

                    try {
                        dispatcher.runSync("createCostComponent", priceContext);
                    } catch (GenericServiceException e) {
                        System.err.println(e.getMessage());
                    }

                    //add supplier to product context
                    Map<String, Object> partyToProductContext = new HashMap<>(context);
                    partyToProductContext.put("userLogin", userLogin);
                    partyToProductContext.put("locale", locale);

                    partyToProductContext.put("partyId", "BigBuy");
                    partyToProductContext.put("productId", id);
                    partyToProductContext.put("roleTypeId", "SUPPLIER");

                    try {
                        dispatcher.runSync("addPartyToProduct", partyToProductContext);
                    } catch (GenericServiceException e) {
                        System.err.println(e.getMessage());
                    }

                    //add product to category context

                    Map<String, Object> productToCategoryContext = new HashMap<>(context);
                    productToCategoryContext.put("userLogin", userLogin);
                    productToCategoryContext.put("locale", locale);
                    productToCategoryContext.put("productCategoryId", categoryId);
                    productToCategoryContext.put("productId", id);

                    try {
                        dispatcher.runSync("addProductToCategory", productToCategoryContext);
                    } catch (GenericServiceException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
        return success;
    }

    public static Map<String, Object> updateBigBuyData(DispatchContext ctx, Map<String, Object> context) throws UnirestException, IOException {
        Map<String, Object> success = ServiceUtil.returnSuccess();

        //1 token: YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA
        //2 token: MGJlMGFlYWJiZjNlOTNkZTk1ZmQyMTA1MjI3NzljMWQwNjFkZTAyNjdhNDA5Y2ExNmJkN2MzOWQ5NzE3YjMyNw
        String token = "YTUwOGI5ZGY5ZDMzNGM4Mjk3ZTY4N2ExODJjYmJiN2VjZDU0ZThiM2Y2NTZkMTlhMjE4NzQ0ZTE4YjgwYjBjNA";

        Unirest.setTimeouts(0, 0);

        com.mashape.unirest.http.JsonNode products = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/products.json?isoCode=en")
                .header("Authorization", "Bearer " + token)
                .asJson().getBody();

        com.mashape.unirest.http.JsonNode productsinformation = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/productsinformation.json?isoCode=en")
                .header("Authorization", "Bearer " + token)
                .asJson().getBody();

        Unirest.setTimeouts(0, 0);
        com.mashape.unirest.http.JsonNode manufacturers = Unirest.get("https://api.sandbox.bigbuy.eu/rest/catalog/manufacturers.json?isoCode=en")
                .header("Authorization", "Bearer " + token)
                .asJson().getBody();

        try {
            FileWriter myWriter = new FileWriter("applications/product/BigBuyData/products.json");
            myWriter.write(products.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("applications/product/BigBuyData/productsinformation.json");
            myWriter.write(productsinformation.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("applications/product/BigBuyData/manufacturers.json");
            myWriter.write(manufacturers.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return success;
    }
}
