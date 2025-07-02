package org.example;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.example.auth.JwtAuthFilter;
import org.example.auth.JwtAuthenticator;
import org.example.auth.RoleAuthorizer;
import org.example.auth.UserPrincipal;
import org.example.core.OrderService;
import org.example.core.ProductService;
import org.example.core.PromoService;
import org.example.core.UserService;
import org.example.db.*;
import org.example.exception.*;
import org.example.resources.OrderResource;
import org.example.resources.ProductResource;
import org.example.resources.PromoResource;
import org.example.resources.UserResource;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class ECommerceApplication extends Application<ECommerceConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ECommerceApplication().run(args);
    }


    private final HibernateBundle<ECommerceConfiguration> hibernate = new HibernateBundle<ECommerceConfiguration>(User.class, Product.class, Order.class, OrderItem.class, Role.class,PromoCode.class,PromoUsage.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(ECommerceConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

    @Override
    public String getName() {
        return "true";
    }

    @Override
    public void initialize(final Bootstrap<ECommerceConfiguration> bootstrap) {
        // TODO: application initialization
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new SwaggerBundle<ECommerceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ECommerceConfiguration configuration) {
                return configuration.getSwagger();
            }
        });
    }

    @Override
    public void run(final ECommerceConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application

        environment.jersey().register(new ProductResource(new ProductService(new ProductDAO(hibernate.getSessionFactory()))));

        environment.jersey().register(new UserResource(new UserService(new UserDAO(hibernate.getSessionFactory()))));
        environment.jersey().register(
                new OrderResource(
                        new OrderService(
                                new OrderDAO(hibernate.getSessionFactory()),
                                new ProductService(
                                        new ProductDAO(hibernate.getSessionFactory())
                                ),
                                new PromoService(
                                        new PromoDAO(hibernate.getSessionFactory()),
                                        new PromoUsageDAO(hibernate.getSessionFactory())
                                )
                        )
                )
        );

        environment.jersey().register(new PromoResource(new PromoService(new PromoDAO(hibernate.getSessionFactory()),new PromoUsageDAO(hibernate.getSessionFactory()))));

        environment.jersey().register(new ProductNotFoundExceptionMappper());

        environment.jersey().register(new IllegalArgumentExceptionMapper());

        environment.jersey().register(new GlobalExceptionMapper());

        environment.jersey().register(new GenericExceptionMapper());

        environment.jersey().register(new OrderFailedExceptionMapper());



        environment.jersey().register(new AuthDynamicFeature(new JwtAuthFilter.Builder<UserPrincipal>()
                .setAuthenticator(new JwtAuthenticator())
                .setAuthorizer(new RoleAuthorizer())
                .setRealm("realm-123").buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(UserPrincipal.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);


    }

}
