package me.devnatan.inventoryframework.example.tournament;

import java.util.List;
import java.util.UUID;

public interface Tournament {
    UUID getId();

    TournamentParticipant getParticipant(UUID id);

    List<TournamentParticipant> getParticipants();
}
