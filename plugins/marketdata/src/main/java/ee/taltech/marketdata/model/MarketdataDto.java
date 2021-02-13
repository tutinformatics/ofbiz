package ee.taltech.marketdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MarketdataDto {
    String registryCode;
    String companyName;
    String address;
    BigDecimal annualRevenue;
    Long employeeCount;
    String emtak;
}
