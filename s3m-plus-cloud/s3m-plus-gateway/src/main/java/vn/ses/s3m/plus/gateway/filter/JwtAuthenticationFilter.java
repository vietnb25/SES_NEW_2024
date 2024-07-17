package vn.ses.s3m.plus.gateway.filter;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import vn.ses.s3m.plus.gateway.util.JwtUtil;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * filter method
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        final List<String> apiEndpoints = List.of("/auth/register", "/auth/login");
        // CHECKSTYLE:OFF
        Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream()
                .noneMatch(uri -> r.getURI()
                        .getPath()
                        .contains(uri));

        if (isApiSecured.test(request)) {
            if (!request.getHeaders()
                    .containsKey("Authorization")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return response.setComplete();

            }

            final String token = request.getHeaders()
                    .getOrEmpty("Authorization")
                    .get(0);
            int validateResult = 0;
            ServerHttpResponse response = exchange.getResponse();
            try {
                validateResult = jwtUtil.validateToken(token);
                response.setRawStatusCode(validateResult);
                if (validateResult != 200) {
                    return response.setComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.setComplete();
            }
        }

        return chain.filter(exchange);
    }

}
