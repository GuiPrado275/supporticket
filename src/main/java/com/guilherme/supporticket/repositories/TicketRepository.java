package com.guilherme.supporticket.repositories;

import com.guilherme.supporticket.models.Ticket;
import com.guilherme.supporticket.models.projection.TicketProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
    SELECT 
        t.id AS id,
        t.user.id AS userId,
        t.description AS description,
        t.peopleInvolved AS peopleInvolved,
        t.isFinished AS isFinished,
        t.registrationDate AS registrationDate
    FROM Ticket t
    WHERE 
        (:isAdmin = false AND t.user.id = :userId) OR 
        (:isAdmin = true AND t.user.id = :userId)      
""")
    List<TicketProjection> findByUser_id(@Param("userId") Long userId,
                                                    @Param("isAdmin") boolean isAdmin);
}
