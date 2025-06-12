package com.guilherme.supporticket.configs;

import com.guilherme.supporticket.security.JWTAuthenticationFilter;
import com.guilherme.supporticket.security.JWTAuthorizationFilter;
import com.guilherme.supporticket.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    private static final String[] PUBLIC_MATCHERS = { //rota livre
            ""
    };

    private static final String[] PUBLIC_MATCHERS_POST = { //user e login são publico para post
            "/user",
            "/login"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {//filterChain recebe o http request
        http.csrf(csrf -> csrf.disable()) //disable the cors and csrf protection
                .cors(cors -> cors.disable());

        AuthenticationManagerBuilder authenticationManagerBuilder = http //add para o builder do authenticationManager
                .getSharedObject(AuthenticationManagerBuilder.class); //com o encriptografia ativada
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
        this.authenticationManager = authenticationManagerBuilder.build();

        http.authorizeHttpRequests(auth -> auth //Qualquer acesso ou rota é autorizado
                .requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
                .requestMatchers(PUBLIC_MATCHERS).permitAll() //Qualquer solicitação de PUBLIC_MATCHERS e POST será permitida
                .anyRequest().authenticated()).authenticationManager(authenticationManager);

        http.addFilter(new JWTAuthenticationFilter(authenticationManager, jwtUtil));
        http.addFilter(new JWTAuthorizationFilter(authenticationManager, jwtUtil, userDetailsService));

        http.sessionManagement(session -> session //A sessão não pode salvar
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() { //cors configuration
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS")); //permitir os métodos
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { //para encriptar
        return new BCryptPasswordEncoder();
    }
}
