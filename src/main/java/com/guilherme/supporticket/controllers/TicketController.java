package com.guilherme.supporticket.controllers;

import com.guilherme.supporticket.models.Ticket;
import com.guilherme.supporticket.models.projection.TicketProjection;
import com.guilherme.supporticket.security.UserSpringSecurity;
import com.guilherme.supporticket.services.TicketService;
import com.guilherme.supporticket.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/ticket")
@Validated
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> findbyId(@PathVariable Long id) {
        Ticket ticket = ticketService.findById(id);
        return ResponseEntity.ok().body(ticket);
    }

    //for adms
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketProjection>> getTicketsByUserId(@PathVariable Long userId) {

        List<TicketProjection> tickets = ticketService.findAllByUser(userId);
        return ResponseEntity.ok(tickets);

    }

    //for normal users
    @GetMapping("/user/me")
    public ResponseEntity<List<TicketProjection>> getMyTrips() {
        UserSpringSecurity user = UserService.autheticated();
        return getTicketsByUserId(user.getId());
    }

    @PostMapping
    @Validated
    public ResponseEntity<Void> create(@Valid @RequestBody Ticket ticket) {
        this.ticketService.create(ticket);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(ticket.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @Validated
    public ResponseEntity<Void> update(@Valid @RequestBody Ticket ticket, @PathVariable Long id) {
        ticket.setId(id);
        this.ticketService.update(ticket);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
