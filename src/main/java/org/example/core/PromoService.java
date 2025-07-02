package org.example.core;

import org.example.api.ApplyPromoCodeDTO;
import org.example.api.PromoCodeDTO;
import org.example.api.PromoUsageDTO;
import org.example.db.PromoCode;
import org.example.db.PromoDAO;
import org.example.db.PromoUsage;
import org.example.db.PromoUsageDAO;
import org.example.exception.OrderFailedException;

import java.time.LocalDate;
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

    public ApplyPromoCodeDTO applyPromoCode(ApplyPromoCodeDTO applyPromoCodeDTO) {

        double discount = 0.0;

        double totalAmount = applyPromoCodeDTO.getTotal();
        if (applyPromoCodeDTO.getPromoCode() != null) {
            PromoCode promoCode = getPromocode(applyPromoCodeDTO.getPromoCode());
            if (!promoCode.isActive()) {
                throw new OrderFailedException("Promo code is inactive");
            }
            LocalDate todayDate = LocalDate.now();
            if (todayDate.isBefore(promoCode.getStartDate()) || todayDate.isAfter(promoCode.getEndDate())) {
                throw new OrderFailedException("Promo code is expired");
            }

            Long totalUsage = getTotalPromoUsage(promoCode.getId());
            if (totalUsage == null) {
                totalUsage = 0L;
            }
            if (totalUsage >= promoCode.getMaxUsageLimit()) {
                throw new OrderFailedException("Max Promo Usage limit exceeded");
            }

            Integer userUsage = getUsageCountForCustomer(promoCode.getId(), applyPromoCodeDTO.getUserId());
            if (userUsage >= promoCode.getMaxUsagePerCustomer()) {
                throw new OrderFailedException("Promo code usage limit exceeded for this customer");
            }


            if (promoCode.getPromoType() == PromoType.PERCENTAGE) {
                discount = totalAmount * promoCode.getDiscount() / 100.0;
                if (discount > promoCode.getMaxDiscount())
                    discount = promoCode.getMaxDiscount();
            } else if (promoCode.getPromoType() == PromoType.FLAT) {
                discount = promoCode.getDiscount();
                if (discount > promoCode.getMaxDiscount())
                    discount = promoCode.getMaxDiscount();
            }
            totalAmount -= discount;
            if (totalAmount < 0) {
                totalAmount = 0;
            }
            Optional<PromoUsage> promoUsageOpt = getPromoUsageByUser(
                    promoCode.getId(),
                    applyPromoCodeDTO.getUserId()
            );

            PromoUsage promoUsage;


            if (promoUsageOpt.isPresent()) {
                promoUsage = promoUsageOpt.get();
                promoUsage.setUsageCount(promoUsage.getUsageCount() + 1);
            } else {
                promoUsage = new PromoUsage();
                promoUsage.setUsageCount(1);
                promoUsage.setUserId(applyPromoCodeDTO.getUserId());
                promoUsage.setPromocode(promoCode);
            }

            PromoUsageDTO promoUsageDTO = new PromoUsageDTO();
            promoUsageDTO.setPromoCodeId(promoCode.getId());
            promoUsageDTO.setUserId(promoUsage.getUserId());
            promoUsageDTO.setUsageCount(promoUsage.getUsageCount());
            saveOrUpdatePromoUsage(promoUsageDTO);
//            order.setMessage("Placed Order with PromoCode"+ promoCode.getCode());
            applyPromoCodeDTO.setDiscountedTotal(totalAmount);
            applyPromoCodeDTO.setMessage("Successfully applied promo code");

        }
        return applyPromoCodeDTO;


    }


    public PromoCode getPromocode(String code) {
        return promoDAO.getPromocodeByPromoCode(code)
                .orElseThrow(() -> new RuntimeException("No promo found with code " + code));
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
