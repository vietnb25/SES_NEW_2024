package vn.ses.s3m.plus.auth.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Class filter xử lý request.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-01-01.
 */
public class AuthorizationFilter extends OncePerRequestFilter {
    /**
     * Hàm filter request.
     *
     * @return void.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
        final FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.equals("/auth/login") || path.equals("/auth/forgot-password") || path.contains("/auth/reset-password")
            || path.contains("/auth/change-password-first-login")) {
            filterChain.doFilter(request, response);
        }
    }

}
