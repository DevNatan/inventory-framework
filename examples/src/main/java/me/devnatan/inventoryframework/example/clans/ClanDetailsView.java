package me.devnatan.inventoryframework.example.clans;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewOpenContext;
import me.devnatan.inventoryframework.ViewRenderContext;
import me.devnatan.inventoryframework.state.StateMarker;
import org.bukkit.Material;

@RequiredArgsConstructor
public final class ClanDetailsView extends View {

	private final ClansManager clansManager;

	private final StateMarker<Clan> clanState = lazy(context -> {
		final String clanTag = context.get(String.class);
		return clansManager.getClan(clanTag);
	});

	@Override
	public void onInit(ViewConfigBuilder config) {
		config.size(3);
	}

	@Override
	public void onOpen(ViewOpenContext ctx) {
		final Clan clan = clanState.get(ctx);
		ctx.setTitle(String.format("[%s] %s", clan.getTag(), clan.getName()));
	}

	@Override
	public void onInitialRender(ViewRenderContext ctx) {
		final Clan clan = clanState.get(ctx);
		ctx.slot(2, 5)
			.withItem(Material.PLAYER_HEAD)
			.onClick(click -> click.open(ClanMemberListView.class, clan));
	}

}
