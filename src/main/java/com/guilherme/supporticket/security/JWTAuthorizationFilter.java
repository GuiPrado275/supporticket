package com.guilherme.supporticket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private JWTUtil jwtUtil;

    private UserDetailsService userDetailsService;

    //construtor
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                                  UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String authorizationHeader = request.getHeader("Authorization");
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);//Isso começa com o Bearer
            UsernamePasswordAuthenticationToken auth = getAuthentication(token);//mas o bearer não é parte do token
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if(this.jwtUtil.isValidToken(token)) { //para autenticar o token, se o token é valido:
            String email = this.jwtUtil.getEmail(token);
            UserDetails user = this.userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(user, null,
                    user.getAuthorities());
            return authenticatedUser; //pega os dados do token para buscar autenticação usando o próprio token,
        }               //recebendo o token verifica se ele é válido e extrai o nome de usuário para procurar o usuário,
        return null;    //retornando assim o que o filtro interno necessita
    }

}
