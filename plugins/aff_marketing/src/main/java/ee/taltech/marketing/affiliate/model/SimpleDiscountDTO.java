package ee.taltech.marketing.affiliate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleDiscountDTO {
    String productPromoId;
    String productCategoryId;
    Double discount;
}
