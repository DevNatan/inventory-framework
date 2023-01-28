package me.devnatan.inventoryframework.example.clans;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface ClansManager {

    @Nullable
    Clan getClan(String tag);

    @Nullable
    Clan getClan(UUID id);

    List<ClanMember> getMembers(UUID clanId);
}
