package com.cs.ganda.config;

import com.cs.ganda.security.JwtAuthenticationEntryPoint;
import com.cs.ganda.security.JwtAuthorizationTokenFilter;
import com.cs.ganda.security.JwtTokenUtil;
import com.cs.ganda.service.AuthenticationDataService;
import com.cs.ganda.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final String tokenHeader;
    private final JwtTokenUtil jwtTokenUtil;
    private final ProfileService profileService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final AuthenticationDataService authenticationDataService;

    public WebSecurityConfig(
            @Value("${jwt.header}") String tokenHeader,
            JwtTokenUtil jwtTokenUtil,
            ProfileService profileService,
            JwtAuthenticationEntryPoint unauthorizedHandler,
            AuthenticationDataService authenticationDataService
    ) {
        this.tokenHeader = tokenHeader;
        this.jwtTokenUtil = jwtTokenUtil;
        this.profileService = profileService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.authenticationDataService = authenticationDataService;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(POST, "/v*/ad/search").permitAll()
                .antMatchers(POST, "/v*/signin").permitAll()
                .antMatchers(POST, "/v*/add-profile").permitAll()
                .antMatchers(POST, "/v*/activate").permitAll()
                .antMatchers(POST, "/v*/activate-phone").permitAll()
                .antMatchers(POST, "/v*/refresh-token").permitAll()
                .antMatchers(POST, "/v*/update-user-profile").permitAll()
                .antMatchers(POST, "/v*/phone-activation-code").permitAll()
                .antMatchers(GET, "/v*/city").permitAll()
                .antMatchers(GET, "/v*/address").permitAll()
                .antMatchers(GET, "/v*/category").permitAll()
                .anyRequest()
                .authenticated()
                .and().httpBasic();

        // Custom JWT based security filter
        JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(profileService, jwtTokenUtil, tokenHeader, authenticationDataService);
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity
                .cors()
                .and()
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        System.out.println(bcrypt.encode("test"));
        return bcrypt;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(profileService);
        return provider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
