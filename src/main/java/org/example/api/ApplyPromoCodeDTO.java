package org.example.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyPromoCodeDTO {

    private Long userId;
    private String promoCode;
    private Double total;
    private Double discountedTotal;
    private String message;

}
