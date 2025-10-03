package com.banco.turnmanagement.events;

import java.time.LocalDateTime;

public record TurnCompleted(
    Long turnId,
    String turnNumber,
    Long cashierId,
    LocalDateTime completedAt
) {}