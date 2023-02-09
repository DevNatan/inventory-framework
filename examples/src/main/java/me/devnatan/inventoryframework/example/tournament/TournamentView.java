package me.devnatan.inventoryframework.example.tournament;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.ViewContext;
import me.devnatan.inventoryframework.state.State;

@RequiredArgsConstructor
public final class TournamentView extends View {

    private final TournamentsManager tournamentsManager;

    private final State<UUID> tournamentIdState = initialState(UUID.class);
    private final State<Tournament> tournamentState = state(this::getTournament);

    @Override
    public void onInit(ViewConfigBuilder config) {
        // TODO schedule update every 5 seconds
    }

    @Override
    public void onOpen(OpenContext ctx) {
        ctx.setTitle(createTitle(ctx));
    }

    @Override
    public void onClose(ViewContext ctx) {
        final Tournament tournament = getTournament(ctx);
        final TournamentParticipant participant =
                tournament.getParticipant(ctx.getPlayer().getUniqueId());
        tournamentsManager.leaveTournament(tournament.getId(), participant.getId());

        for (final IFContext other : getContexts()) other.updateTitle(createTitle(other));
    }

    private Tournament getTournament(ViewContext context) {
        return tournamentsManager.getTournament(tournamentIdState.get(context));
    }

    private String createTitle(IFContext context) {
        return String.format(
                "Tournament - %d participants",
                tournamentState.get(context).getParticipants().size());
    }
}
