package ee.taltech.marketdata.service;

import ee.taltech.marketdata.model.MarketdataDto;
import ee.taltech.marketing.affiliate.model.SimpleDiscountDTO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

public class MarketdataService {

    public static final String module = MarketdataService.class.getName();

    // Entity PartyGroup with its relevant fields
    public static final String PARTY_GROUP_ENTITY = "PartyGroup";

    public static final String PARTY_ID = "partyId";
    public static final String GROUP_NAME = "groupName";
    public static final String ANNUAL_REVENUE = "annualRevenue";
    public static final String NUM_EMPLOYEES = "numEmployees";

    private static Pattern registryCodeRegex = Pattern.compile("\\d+");


    public MarketdataService() throws GenericEntityException {
        Delegator delegator = DelegatorFactory.getDelegator("default");

        String id = delegator.getNextSeqId(PARTY_GROUP_ENTITY);
        GenericValue demoCompany = delegator.makeValue(PARTY_GROUP_ENTITY, UtilMisc.toMap(
                PARTY_ID, id,
                GROUP_NAME, "DemoEttevõtte",
                ANNUAL_REVENUE, "100 000",
                NUM_EMPLOYEES, "20")
        );

        delegator.create(demoCompany);
    }

    public static Map<String, List<MarketdataDto>> getMarketdataCompanies(DispatchContext dctx, Map<String, ?> context)
            throws GenericEntityException {

        Delegator delegator = dctx.getDelegator();

        List<GenericValue> companies = EntityQuery.use(delegator).from(PARTY_GROUP_ENTITY).queryList();
//        GenericValue company = EntityQuery.use(delegator).from(PARTY_GROUP_ENTITY).where(PARTY_ID, "1234567").queryOne();

        List<MarketdataDto> companyList = new ArrayList<>();

        for (GenericValue company : companies) {
//            if (company.get(PARTY_ID).getClass().equals(BigDecimal.class)) {
//            String groupName = null;
//            String id = company.get(PARTY_ID).toString();
//            if (company.get(GROUP_NAME) != null) {
//                groupName = company.get(GROUP_NAME).toString();
//            }
                if (registryCodeRegex.matcher((String) company.get(PARTY_ID)).matches()) {
                    companyList.add(new MarketdataDto(
                            (String) company.get(PARTY_ID),
                            (String) company.get(GROUP_NAME),
                            (BigDecimal) company.get(ANNUAL_REVENUE),
                            (Long) company.get(NUM_EMPLOYEES))
                    );
                }
//            }

//            companyList.add(getMarketdataCompany(dctx, new HashMap<>(Map.of(PRODUCT_CATEGORY_ID, productPromo.get(PROMO_TEXT)))).get(DISCOUNT));
//            MarketdataDto marketdataDto= new MarketdataDto(companies.get(PARTY_ID), companies.get(GROUP_NAME));
        }
//        System.out.println("HEY!");
//        System.out.println(company.get(PARTY_ID));



        return new HashMap<>(Map.of("companies", companyList));
    }

//    public static Map<String, MarketdataDto> getMarketdataCompany(DispatchContext dctx, Map<String, ?> context)
//            throws GenericEntityException {
//
//        Delegator delegator = dctx.getDelegator();
//
//        GenericValue company = EntityQuery.use(delegator).from(PARTY_GROUP_ENTITY)
//
//        return new HashMap<>(Map.of("company", new MarketdataDto()));
//    }

    public static Map<String, Object> createMarketdataEntry(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        try {
            GenericValue ofbizDemo = delegator.makeValue("MarketdataModel");
            // Auto generating next sequence of ofbizDemoId primary key
            ofbizDemo.setNextSeqId();
            // Setting up all non primary key field values from context map
            ofbizDemo.setNonPKFields(context);
            // Creating record in database for OfbizDemo entity for prepared value
            ofbizDemo = delegator.create(ofbizDemo);
            result.put("companyId", ofbizDemo.getString("companyId"));
            Debug.log("==========This is my first Java Service implementation in Apache OFBiz. OfbizDemo record created successfully with ofbizDemoId:"+ofbizDemo.getString("ofbizDemoId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error in creating record in OfbizDemo entity ........" +module);
        }
        return result;
    }

//    public static void createMarketdataEntry() {
//        Debug.log("==========This is marketdata service!===========");
//    }

    public static final String resource = "PartyUiLabels";

    public static Map<String, Object> importCsv(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
        String encoding = System.getProperty("file.encoding");
        String csvFile = Charset.forName(encoding).decode(fileBytes).toString();
        csvFile = csvFile.replaceAll("\\r", "");
        String[] records = csvFile.split("\\n");

        for (int i = 0; i < records.length; i++) {
            if (records[i] != null) {
                String str = records[i].trim();
                String[] map = str.split(",");
                if (map.length != 2 && map.length != 3) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "PartyImportInvalidCsvFile", locale));
                }
                GenericValue addrMap = delegator.makeValue("AddressMatchMap");
                addrMap.put("mapKey", map[0].trim().toUpperCase(Locale.getDefault()));
                addrMap.put("mapValue", map[1].trim().toUpperCase(Locale.getDefault()));
                int seq = i + 1;
                if (map.length == 3) {
                    char[] chars = map[2].toCharArray();
                    boolean isNumber = true;
                    for (char c : chars) {
                        if (!Character.isDigit(c)) {
                            isNumber = false;
                        }
                    }
                    if (isNumber) {
                        try {
                            seq = Integer.parseInt(map[2]);
                        } catch (Throwable t) {
                            Debug.logWarning(t, "Unable to parse number", module);
                        }
                    }
                }

                addrMap.put("sequenceNum", (long) seq);
                Debug.logInfo("Creating map entry: " + addrMap, module);
                try {
                    delegator.create(addrMap);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
            } else {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "PartyImportNoRecordsFoundInFile", locale));
            }
        }

        return ServiceUtil.returnSuccess();
    }

}
