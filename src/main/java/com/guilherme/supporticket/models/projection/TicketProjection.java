package com.guilherme.supporticket.models.projection;

import java.time.LocalDateTime;

public interface TicketProjection {

    Long getId();

    Long getTicketId();

    String getDescription();

    String getPeopleAndSetorInvolved();

    boolean isFinished();

    LocalDateTime getRegistrationDate();

}
