package com.guilherme.supporticket.services;

import com.guilherme.supporticket.models.User;
import com.guilherme.supporticket.models.enums.ProfileEnum;
import com.guilherme.supporticket.repositories.UserRepository;
import com.guilherme.supporticket.security.UserSpringSecurity;
import com.guilherme.supporticket.services.exceptions.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id){
        UserSpringSecurity userSpringSecurity = autheticated();
        if (!Objects.isNull(userSpringSecurity)
        || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId())) {
            throw new AuthorizationException("Acesso negado");

            //parei aqui
        }
    }

    public static UserSpringSecurity autheticated(){
        try{
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return null;
        }
    }

}
