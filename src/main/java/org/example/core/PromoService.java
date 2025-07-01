package org.example.core;

import org.example.api.PromoCodeDTO;
import org.example.db.PromoCode;
import org.example.db.PromoDAO;

public class PromoService {
    private final PromoDAO promoDAO;

    public PromoService(PromoDAO promoDAO) {
        this.promoDAO = promoDAO;
    }


    public PromoCode createPromocode(PromoCodeDTO promoCodeDTO) {

        PromoCode promoCode = new PromoCode();
        promoCode.setCode(promoCodeDTO.getCode());
        promoCode.setPromoType(promoCodeDTO.getPromoType());
        promoCode.setDiscount(promoCodeDTO.getDiscount());
        promoCode.setMaxDiscount(promoCodeDTO.getMaxDiscount());
        promoCode.setStartDate(promoCodeDTO.getStartDate());
        promoCode.setEndDate(promoCodeDTO.getEndDate());
        promoCode.setActive(promoCodeDTO.isActive());
        promoCode.setMaxUsageLimit(promoCodeDTO.getMaxUsageLimit());
        promoCode.setMaxUsagePerCustomer(promoCodeDTO.getMaxUsagePerCustomer());
        return promoDAO.saveOrUpdate(promoCode);
    }
}
