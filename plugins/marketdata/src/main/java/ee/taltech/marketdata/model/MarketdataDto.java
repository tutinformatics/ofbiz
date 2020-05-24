package ee.taltech.marketdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
//@AllArgsConstructor
public class MarketdataDto {
    String partyId;

    String companyName;
//    String registryCode;
//    String companyStatus;
//    String companyAddress; // List?
//    String companyBusinessModel;
//    String companySector;
    BigDecimal annualRevenue;
    Long employeeCount;

//    List<PersonDto> persons; // List?


    public MarketdataDto(String partyId, String companyName, BigDecimal annualRevenue, Long employeeCount) {
        this.partyId = partyId;
        this.companyName = companyName;
        this.annualRevenue = annualRevenue;
        this.employeeCount = employeeCount;
    }
}
