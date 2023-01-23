package me.devnatan.inventoryframework.example.clans;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.BukkitItem;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContext;
import me.devnatan.inventoryframework.ViewOpenContext;
import me.devnatan.inventoryframework.ViewRenderContext;
import me.devnatan.inventoryframework.state.Pagination;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public final class ClanMemberListView extends View {

	private final ClansManager clansManager;

	private final State<Clan> clanState = initial(Clan.class);
	private final State<List<ClanMember>> membersListState = state(this::createMemberList);
	private final Pagination<ClanMember> pagination = pagination(membersListState, this::onItemRender);

	@Override
	public void onInit(ViewConfigBuilder config) {
		config.size(6).layout(
			"(XXXXXXXX",
			"XOOOOOOOX",
			"XOOOOOOOX",
			"XOOOOOOOX",
			"XOOOOOOOX",
			"XX<XXX>XX"
		);
	}

	@Override
	public void onOpen(ViewOpenContext ctx) {
		final Clan clan = clanState.get(ctx);
		final List<ClanMember> memberList = membersListState.get(ctx);
		ctx.setTitle(String.format("[%s] Members (%d)", clan.getTag(), memberList.size()));
	}

	@Override
	public void onRender(ViewRenderContext ctx) {
		ctx.slot(NAVIGATE_BACKWARDS).onClick(pagination::back);
		ctx.slot(NAVIGATE_FORWARD).onClick(pagination::advance);
	}

	private List<ClanMember> createMemberList(ViewContext context) {
		return clansManager.getMembers(clanState.get(context).getId());
	}

	private void onItemRender(BukkitItem item, ClanMember member) {
		final ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
		final SkullMeta meta = (SkullMeta) requireNonNull(stack.getItemMeta());

		meta.setDisplayName(member.getName());
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUUID()));
		stack.setItemMeta(meta);

		item.withItem(stack);
	}

}
