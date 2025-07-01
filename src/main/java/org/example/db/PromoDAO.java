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

    public Optional<PromoCode> getPromocodeByPromoCode(String promoCode) {
//        return currentSession().createQuery("from PromoCode p where p.code  =: promoCode", PromoCode.class)
//                .setParameter("promoCode", promoCode)
//                .uniqueResultOptional();
        return null;
    }


}
