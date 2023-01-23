package me.devnatan.inventoryframework.example.clans;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ClansManager {

	@Nullable
	Clan getClan(String tag);

	List<ClanMember> getMembers(UUID clanId);

}
