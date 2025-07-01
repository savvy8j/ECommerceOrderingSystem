package org.example.api;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.PromoType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PromoCodeDTO {
    private String code;

    private PromoType promoType;

    private Long discount;

    private Long maxDiscount;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean active;

    private Integer maxUsageLimit;

    private Integer maxUsagePerCustomer;

}
