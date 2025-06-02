package com.guilherme.supporticket.services;

import com.guilherme.supporticket.models.Ticket;
import com.guilherme.supporticket.models.User;
import com.guilherme.supporticket.models.enums.ProfileEnum;
import com.guilherme.supporticket.models.projection.TicketProjection;
import com.guilherme.supporticket.repositories.TicketRepository;
import com.guilherme.supporticket.security.UserSpringSecurity;
import com.guilherme.supporticket.services.exceptions.AuthorizationException;
import com.guilherme.supporticket.services.exceptions.DataBindingViolationException;
import com.guilherme.supporticket.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    public Ticket findById(Long id){
        Ticket ticket = this.ticketRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
                "Ticket não encontrado! Id: " + id + ", Tipo: " + Ticket.class.getName()));

        UserSpringSecurity userSpringSecurity = UserService.autheticated();
        if(Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN)
            && !userHasTicket(userSpringSecurity, ticket)){
            throw new AuthorizationException("Acesso negado!");
        }
        return ticket;
    }

    public List<TicketProjection> findAllByUser(Long userId){
        UserSpringSecurity userSpringSecurity = UserService.autheticated();
        if(userSpringSecurity == null){
            throw new AuthorizationException("Acesso negado!");
        }

        boolean isAdmin = userSpringSecurity.hasRole(ProfileEnum.ADMIN);

        if(!isAdmin){
            if (userId != null && !userId.equals(userSpringSecurity.getId())){
                throw new AuthorizationException("Acesso negado!");
            }
            userId = userSpringSecurity.getId();
        }
        if (isAdmin && userId == null){
            throw new IllegalArgumentException("User Id é obrigatório para um ADM");
        }
        return ticketRepository.findByUser_id(userId, isAdmin);
    }

    @Transactional
    public Ticket create(Ticket ticket){
        UserSpringSecurity userSpringSecurity = UserService.autheticated();
        if(Objects.isNull(userSpringSecurity)){
            throw new AuthorizationException("Acesso negado!");
        }
        User user = userService.findById(userSpringSecurity.getId()); //to ensure that a bad user can't use the
        ticket.setId(null);                                           //id in ticket creation
        ticket.setUser(user);
        ticket = this.ticketRepository.save(ticket);
        return ticket;
    }

    @Transactional
    public Ticket update(Ticket updatedTicket){
        Ticket existingTicket = findById(updatedTicket.getId()); //to ensure that the ticket exist
        existingTicket.setPeopleAndSetorInvolved(updatedTicket.getPeopleAndSetorInvolved());
        existingTicket.setDescription(updatedTicket.getDescription());
        existingTicket.setRegistrationDate(updatedTicket.getRegistrationDate());
        existingTicket.setFinished(updatedTicket.isFinished());

        return this.ticketRepository.save(existingTicket);
    }

    public void delete(Long id){
        findById(id);
        try{
            ticketRepository.deleteById(id);
        } catch (Exception e){
            throw new DataBindingViolationException("Ticket não encontrado! Id: " + id);
        }
    }

    private boolean userHasTicket(UserSpringSecurity userSpringSecurity, Ticket ticket){
        return ticket.getUser().getId().equals(userSpringSecurity.getId());
    }
}
