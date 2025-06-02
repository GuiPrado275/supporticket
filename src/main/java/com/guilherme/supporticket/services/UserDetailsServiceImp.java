package com.guilherme.supporticket.services;

import com.guilherme.supporticket.models.User;
import com.guilherme.supporticket.repositories.UserRepository;
import com.guilherme.supporticket.security.UserSpringSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email);

        if(Objects.isNull(user)){
            throw new UsernameNotFoundException("Email não encontrado: " + email);
        }
        return new UserSpringSecurity(user.getId(), user.getEmail(), user.getPassword(), user.getProfiles());
    } //Isso vai para o UserDetaisService que transforma o ID, nome de usuário, senha e perfis em autoridades
}
