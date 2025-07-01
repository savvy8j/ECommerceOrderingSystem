package org.example.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.PromoType;

import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "promocode")
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)
    private PromoType promoType;

    private Long discount;

    private Long maxDiscount;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isActive;

    private Integer maxUsageLimit;

    private Integer maxUsagePerCustomer;
}
