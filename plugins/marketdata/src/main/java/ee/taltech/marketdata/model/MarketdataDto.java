package ee.taltech.marketdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
//@AllArgsConstructor
public class MarketdataDto {
    String registryCode;

    String companyName;
//    String companyStatus;
//    String companyAddress; // List?
//    String companyBusinessModel;
//    String companySector;
    BigDecimal annualRevenue;
    Long employeeCount;

//    List<PersonDto> persons; // List?


    public MarketdataDto(String registryCode, String companyName, BigDecimal annualRevenue, Long employeeCount) {
        this.registryCode = registryCode;
        this.companyName = companyName;
        this.annualRevenue = annualRevenue;
        this.employeeCount = employeeCount;
    }
}
