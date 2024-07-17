package vn.ses.s3m.plus.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.ses.s3m.plus.auth.service.UserDetailsService;

/**
 * Class config Security.
 *
 * @author Arius Vietnam JSC.
 * @since 2022-01-01.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Hàm tạo BCryptPasswordEncoder.
     *
     * @return PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Hàm lấy authentication.
     *
     * @return AuthenticationManager.
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Hàm config password encoder cho user.
     *
     * @param AuthenticationManagerBuilder auth
     * @return void.
     */
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Hàm config authen cho http request.
     *
     * @param AuthenticationManagerBuilder auth
     * @return void.
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable();
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
            .antMatchers("/auth/**")
            .permitAll();
        http.authorizeRequests()
            .anyRequest()
            .authenticated();
        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
