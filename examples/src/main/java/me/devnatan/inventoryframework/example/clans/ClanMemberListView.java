package me.devnatan.inventoryframework.example.clans;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.BukkitItem;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewOpenContext;
import me.devnatan.inventoryframework.ViewRenderContext;
import me.devnatan.inventoryframework.state.Pagination;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@RequiredArgsConstructor
public final class ClanMemberListView extends View {

    private final ClansManager clansManager;
    private final State<List<ClanMember>> membersListState = state(ctx -> clansManager.getMembers(ctx.get(UUID.class)));

    private final State<Clan> clanState = state(ctx -> clansManager.getClan(ctx.get(UUID.class)));
    private final Pagination pagination = pagination(membersListState, this::createPaginationItem);

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.size(6).layout("        ", " OOOOOOO ", " OOOOOOO ", " OOOOOOO ", " OOOOOOO ", "  <   >  ");
    }

    @Override
    public void onOpen(ViewOpenContext ctx) {
        final Clan clan = clanState.get(ctx);
        final List<ClanMember> memberList = membersListState.get(ctx);
        ctx.setTitle(String.format("[%s] Members (%d)", clan.getTag(), memberList.size()));
    }

    @Override
    public void onInitialRender(ViewRenderContext ctx) {
        ctx.layoutSlot("<").clicked(pagination::back);
        ctx.layoutSlot(">").clicked(pagination::advance);
    }

    private void createPaginationItem(BukkitItem item, ClanMember member) {
        final ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta meta = (SkullMeta) requireNonNull(stack.getItemMeta());

        meta.setDisplayName(member.getName());
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(member.getUUID()));
        stack.setItemMeta(meta);

        item.withItem(stack);
    }
}
