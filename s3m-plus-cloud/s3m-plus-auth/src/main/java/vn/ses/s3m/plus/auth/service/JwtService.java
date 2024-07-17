package vn.ses.s3m.plus.auth.service;

import java.util.Date;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    // 60 min
    public static final int EXPIRE_TIME = 1000 * 60 * 60 * 24;

    /**
     * Hàm tạo JWT. *
     *
     * @return String JWT.
     */
    public String createJwt(final UserDetails userDetails) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String accessToken = JWT.create()
            .withSubject(userDetails.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME))
            .withClaim("roles", userDetails.getAuthorities()
                // CHECKSTYLE:OFF
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            // CHECKSTYLE:ON
            .sign(algorithm);
        return accessToken;
    }
}
