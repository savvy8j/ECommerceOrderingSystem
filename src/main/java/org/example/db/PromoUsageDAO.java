package org.example.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class PromoUsageDAO extends AbstractDAO<PromoUsage> {
    public PromoUsageDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public PromoUsage saveOrUpdate(PromoUsage promoUsage) {
        return persist(promoUsage);
    }


    public Integer getUsageCountForCustomer(Long promoCodeId, Long userId) {
        return currentSession().createQuery(
                        " SELECT u.usageCount FROM PromoUsage  u WHERE u.promocode.id = :promoCodeId AND u.userId = :userId",
                        Integer.class
                )
                .setParameter("promoCodeId", promoCodeId)
                .setParameter("userId", userId)
                .uniqueResultOptional()
                .orElse(0);

    }
    public Optional<PromoUsage> getPromoUsageByUser(Long promoCodeId, Long userId) {
        return
                        currentSession().createQuery(
                                        "FROM PromoUsage u WHERE u.promocode.id = :promoCodeId AND u.userId = :userId",
                                        PromoUsage.class
                                )
                                .setParameter("promoCodeId", promoCodeId)
                                .setParameter("userId", userId)
                                .uniqueResultOptional();
    }

    public Long getTotalPromoUsage(Long promoCodeId) {
        return currentSession()
                .createQuery("SELECT SUM(p.usageCount) FROM  PromoUsage p where p.promocode.id =:promoCodeId", Long.class)
                .setParameter("promoCodeId", promoCodeId)
                .getSingleResult();

    }


}
