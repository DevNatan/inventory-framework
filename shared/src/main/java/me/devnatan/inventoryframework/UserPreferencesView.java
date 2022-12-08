package me.devnatan.inventoryframework;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.pagination.Paginated;
import me.devnatan.inventoryframework.state.MultitonState;
import me.devnatan.inventoryframework.state.State;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

@RequiredArgsConstructor
public final class UserPreferencesView extends View {

	private final UserPreferencesRegistry userPreferencesRegistry;
	private final State<UserPreferences> preferences
		= multitonState(userPreferencesRegistry::getPreferences);

	@Override
	protected void onRender(@NotNull ViewContext context) {
		preferences.

		for (final PreferenceType preferenceType : PreferenceType.values()) {
			final Preference preference = null;

			context.availableSlot()
				.rendered(() -> createPreferenceItem(preference))
				.onClick(click -> {
					/* TODO */
				});
		}
	}

	private ItemStack createPreferenceItem(Preference preference) {
		return new ItemStack(Material.GOLD_INGOT);
	}

}

interface UserPreferencesRegistry {

	UserPreferences getPreferences(UUID playerUuid);

}

interface UserPreferences {}

class Preference {}

enum PreferenceType {}