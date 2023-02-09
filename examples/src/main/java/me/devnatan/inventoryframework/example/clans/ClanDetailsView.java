package me.devnatan.inventoryframework.example.clans;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class ClanDetailsView extends View {

	private final ClansManager clansManager;
	private final State<Clan> clanState;

	public ClanDetailsView(@NotNull ClansManager clansManager) {
		this.clansManager = clansManager;
		this.clanState = state(context -> {
			// TODO fix this example using context.get(...);
			final String clanTag = "";
			return clansManager.getClan(clanTag);
		});
	}

	@Override
	public void onInit(ViewConfigBuilder config) {
		config.size(3);
	}

	@Override
	public void onOpen(OpenContext ctx) {
		final Clan clan = clanState.get(ctx);
		ctx.setTitle(String.format("[%s] %s", clan.getTag(), clan.getName()));
	}

	@Override
	public void onFirstRender(RenderContext ctx) {
		final Clan clan = clanState.get(ctx);
		ctx.slot(2, 5).withItem(Material.PLAYER_HEAD).clicked(click -> click.open(ClanMemberListView.class));
	}
}
