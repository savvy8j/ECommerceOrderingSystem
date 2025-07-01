package org.example.core;

import org.example.api.PromoCodeDTO;
import org.example.api.PromoUsageDTO;
import org.example.db.PromoCode;
import org.example.db.PromoDAO;
import org.example.db.PromoUsage;
import org.example.db.PromoUsageDAO;

import java.util.Optional;

public class PromoService {
    private final PromoDAO promoDAO;
    private final PromoUsageDAO promoUsageDAO;

    public PromoService(PromoDAO promoDAO, PromoUsageDAO promoUsageDAO) {
        this.promoDAO = promoDAO;
        this.promoUsageDAO = promoUsageDAO;
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

    public void saveOrUpdatePromoUsage(PromoUsageDTO promoUsageDTO) {
        PromoUsage promoUsage;

        Optional<PromoUsage> existing = promoUsageDAO.getPromoUsageByUser(
                promoUsageDTO.getPromoCodeId(),
                promoUsageDTO.getUserId()
        );

        if (existing.isPresent()) {
            promoUsage = existing.get();
            promoUsage.setUsageCount(promoUsageDTO.getUsageCount());
        } else {
            promoUsage = new PromoUsage();
            promoUsage.setUserId(promoUsageDTO.getUserId());
            promoUsage.setUsageCount(promoUsageDTO.getUsageCount());

            PromoCode promoCode = promoDAO.findById(promoUsageDTO.getPromoCodeId());
            promoUsage.setPromocode(promoCode);
        }

        promoUsageDAO.saveOrUpdate(promoUsage);
    }


    public PromoCode getPromocode(String code) {
        return promoDAO.getPromocodeByPromoCode(code)
                .orElseThrow(()-> new RuntimeException("No promo found with code " + code));
    }

    public Optional<PromoUsage> getPromoUsageByUser(Long promoCodeId, Long userId) {
        return promoUsageDAO.getPromoUsageByUser(promoCodeId, userId);
    }

    public Integer getUsageCountForCustomer(Long promoCodeId, Long userId) {
        return promoUsageDAO.getUsageCountForCustomer(promoCodeId, userId);
    }

    public Long getTotalPromoUsage(Long promoCodeId) {
        return promoUsageDAO.getTotalPromoUsage(promoCodeId);
    }


}
