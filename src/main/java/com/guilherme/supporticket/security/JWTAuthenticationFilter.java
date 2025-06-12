package com.guilherme.supporticket.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilherme.supporticket.exceptions.GlobalExceptionHandler;
import com.guilherme.supporticket.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

//recebe o /login
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;

    //construtor
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager
            , JWTUtil jwtUtil) {
        setAuthenticationFailureHandler(new GlobalExceptionHandler());
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    //tentativa para validação do email e a senha
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            User userCredentials = new ObjectMapper().readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userCredentials.getEmail(), userCredentials.getPassword(), new ArrayList<>());

            Authentication authentication = authenticationManager.authenticate(authToken);
            return authentication;
        } catch (IOException e){ //se a validação falhar
            throw new RuntimeException(e);
        }
    }

    @Override //se a authenticação for um sucesso, vai rodar isso:
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain,
                                            Authentication authentication) throws IOException, ServletException {
        UserSpringSecurity userSpringSecurity = (UserSpringSecurity) authentication.getPrincipal();
        String email = userSpringSecurity.getUsername();
        String token = jwtUtil.generateToken(email);
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
    } //isso é para retornar para o user o token para ser usado na authenticação da rota

}
