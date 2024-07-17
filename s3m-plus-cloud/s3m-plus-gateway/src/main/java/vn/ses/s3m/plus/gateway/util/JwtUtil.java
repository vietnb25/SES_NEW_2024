package vn.ses.s3m.plus.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public static final int OK_CODE = 200;
    public static final int EXPIRED_CODE = 777;

    /**
     * get claim info method
     *
     * @author LongLT
     * @since Oct 31, 2022
     * @param token
     * @return
     */
    public DecodedJWT getClaims(final String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        } catch (Exception e) {
            System.out.println(e.getMessage() + " => " + e);
        }
        return null;
    }

    /**
     *
     * @author Wasiq Bhamla
     * @since Oct 31, 2022
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public int validateToken(final String token) throws JWTVerificationException {
        int result = OK_CODE;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException ex // - if the token has expired.
        ) {
            System.out.println("the token has expired.");
            result = EXPIRED_CODE;
        }

        return result;
    }

}
