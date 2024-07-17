package vn.ses.s3m.plus.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.ses.s3m.plus.gateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {
    @Autowired
    private JwtAuthenticationFilter filter;

    /**
     * route method
     *
     * @author LongLT
     * @since Oct 31, 2022
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator routes(final RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth", r -> r.path("/auth/**")
                .uri("lb://AUTH"))
            .route("load", r -> r.path("/load/**")
                .filters(f -> f.filter(filter))
                .uri("lb://SERVICES-CONSUMER"))
            .route("pv", r -> r.path("/pv/**")
                .filters(f -> f.filter(filter))
                .uri("lb://SERVICES-CONSUMER"))
            .route("grid", r -> r.path("/grid/**")
                .filters(f -> f.filter(filter))
                .uri("lb://SERVICES-CONSUMER"))
            .route("operation", r -> r.path("/operation/**")
                .filters(f -> f.filter(filter))
                .uri("lb://SERVICES-CONSUMER"))
            .route("common", r -> r.path("/common/**")
                .filters(f -> f.filter(filter))
                .uri("lb://SERVICES-CONSUMER"))
            .build();
    }

}
