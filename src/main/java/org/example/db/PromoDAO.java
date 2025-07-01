package org.example.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class PromoDAO extends AbstractDAO<PromoCode> {
    public PromoDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public PromoCode saveOrUpdate(PromoCode promoCode) {
        return persist(promoCode);
    }
    public PromoCode findById(Long id) {
        return get(id);
    }

    public Optional<PromoUsage> getPromoUsageByUser(Long promoCodeId, Long userId) {
        return currentSession().createQuery(
                                        "FROM PromoUsage u WHERE u.promocode.id = :promoCodeId AND u.userId = :userId",
                                        PromoUsage.class
                                )
                                .setParameter("promoCodeId", promoCodeId)
                                .setParameter("userId", userId)
                                .uniqueResultOptional();
    }

    public Optional<PromoCode> getPromocodeByPromoCode(String promoCode) {
        return currentSession().createQuery("from PromoCode p where p.code  =: promoCode", PromoCode.class)
                .setParameter("promoCode", promoCode)
                .uniqueResultOptional();

    }


}
