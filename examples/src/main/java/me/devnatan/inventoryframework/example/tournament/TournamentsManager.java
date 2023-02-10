package me.devnatan.inventoryframework.example.tournament;

import java.util.UUID;

public interface TournamentsManager {
    Tournament getTournament(UUID id);

    void leaveTournament(UUID tournamentId, UUID participantId);
}
