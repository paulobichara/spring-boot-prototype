package org.paulobichara.prototype.config.security;

import org.paulobichara.prototype.security.CustomUserDetailsService;
import org.paulobichara.prototype.security.JwtAuthenticationFilter;
import org.paulobichara.prototype.security.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService authService;

    @Autowired
    private JwtServerProperties jwtServerProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable().antMatcher("/**")
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/v2/api-docs", "/swagger-ui.html**", "/webjars/**", "/swagger-resources/**")
                    .permitAll()
            .antMatchers(HttpMethod.POST, "/api/users")
                    .permitAll()
            .anyRequest().authenticated()
            .and().formLogin().disable()
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtServerProperties))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtServerProperties))
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
