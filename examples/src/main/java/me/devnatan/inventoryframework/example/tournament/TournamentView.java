package me.devnatan.inventoryframework.example.tournament;

import java.util.UUID;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.state.State;

public final class TournamentView extends View {

    private final TournamentsManager tournamentsManager;

    private final State<UUID> idState = initialState(UUID.class);
    private final State<Tournament> tournamentState;

    public TournamentView(TournamentsManager tournamentsManager) {
        this.tournamentsManager = tournamentsManager;
        this.tournamentState = state(ctx -> tournamentsManager.getTournament(idState.get(ctx)));
    }

    @Override
    public void onInit(ViewConfigBuilder config) {
        // TODO schedule update every 5 seconds
    }

    @Override
    public void onOpen(OpenContext ctx) {
        ctx.setTitle(createTitle(ctx));
    }

    @Override
    public void onClose(CloseContext ctx) {
        final Tournament tournament = tournamentState.get(ctx);
        final TournamentParticipant participant =
                tournament.getParticipant(ctx.getPlayer().getUniqueId());
        tournamentsManager.leaveTournament(tournament.getId(), participant.getId());

        for (final IFContext other : getContexts()) other.updateTitle(createTitle(other));
    }

    private String createTitle(IFContext context) {
        final Tournament tournament = tournamentState.get(context);
        return String.format(
                "Tournament - %d participants", tournament.getParticipants().size());
    }
}
