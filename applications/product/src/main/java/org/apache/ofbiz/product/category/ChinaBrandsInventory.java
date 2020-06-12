package org.apache.ofbiz.product.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ChinaBrandsInventory {
    public static Map<String, Object> addChinaBrandsCatalogAndSupplier(DispatchContext ctx, Map<String, Object> context) throws UnirestException, JsonProcessingException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        //supplier name
        String partyId = "ChinaBrands";
        //catalog name
        String catalogNameAndId = "ChinaBrandsCatalog";

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
    public static Map<String, Object> addChinaBrandsCategory(DispatchContext ctx, Map<String, Object> context) throws UnirestException, IOException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        ObjectMapper objectMapper = new ObjectMapper();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String productCategoryTypeId = (String) context.get("productCategoryTypeId");
        String ofbizId = productCategoryTypeId + "CB";

        //category to catalog context
        Map<String, Object> contextCopyCategoryToCatalog = new HashMap<>(context);
        contextCopyCategoryToCatalog.put("userLogin", userLogin);
        contextCopyCategoryToCatalog.put("locale", locale);
        contextCopyCategoryToCatalog.put("prodCatalogCategoryTypeId", "PCCT_PURCH_ALLW");
        contextCopyCategoryToCatalog.put("prodCatalogId", "ChinaBrandsCatalog");
        contextCopyCategoryToCatalog.put("productCategoryId", ofbizId);

        Map<String, Object> contextSupplierToCategory = new HashMap<>(context);
        contextSupplierToCategory.put("userLogin", userLogin);
        contextSupplierToCategory.put("locale", locale);
        contextSupplierToCategory.put("partyId", "ChinaBrands");
        contextSupplierToCategory.put("productCategoryId", ofbizId);
        contextSupplierToCategory.put("roleTypeId", "SUPPLIER");


        //  Adding Category
        String filename = "applications/product/ChinaBrandsData/ChinaBrandsCategories.json";
        com.fasterxml.jackson.databind.JsonNode categoryObj = objectMapper.readTree(new FileReader(filename));
        JsonNode categoryList = categoryObj.get("msg");

        for (JsonNode category : categoryList) {
            if (String.valueOf(category.get("cat_id")).replace("\"", "").equals(productCategoryTypeId)) {
                context.put("categoryName", String.valueOf(category.get("cat_name")).replace("\"", ""));
                context.put("productCategoryId", ofbizId);
                context.put("productCategoryTypeId", "CATALOG_CATEGORY");

                try {
                    dispatcher.runSync("createProductCategory", context);
                } catch (GenericServiceException e) {
                    System.err.println(e.getMessage());
                }
                break;
            }
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
    public static Map<String, Object> addProductsCB(DispatchContext ctx, Map<String, Object> context) throws UnirestException, IOException {
        Map<String, Object> success = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        ObjectMapper objectMapper = new ObjectMapper();
        return success;
    }
}
