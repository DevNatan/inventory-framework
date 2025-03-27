@file:JvmSynthetic

package me.devnatan.inventoryframework

import org.jetbrains.annotations.ApiStatus
import kotlin.time.Duration
import kotlin.time.toJavaDuration

private const val SECONDS_TO_TICKS_DIV = 20L

/**
 * Schedules the view to update every fixed interval.
 *
 * @param interval The interval.
 * @return This configuration builder.
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/scheduled-updates">Scheduled
 *   Updates on Wiki</a>
 */
public fun ViewConfigBuilder.scheduleUpdate(interval: Duration): ViewConfigBuilder =
    scheduleUpdate(
        interval.inWholeSeconds.takeIf { value -> value > 0 }?.div(SECONDS_TO_TICKS_DIV) ?: 0,
    )

/**
 * Waits a fixed delay before any player interaction.
 *
 * Interactions called before delay completion are cancelled.
 *
 * ***This API is experimental and is not subject to the general compatibility guarantees such API
 * may be changed or may be removed completely in any further release.***
 *
 * @param interactionDelay Duration of the interaction delay or `null` to reset.
 * @return This configuration builder.
 */
@ApiStatus.Experimental
public fun ViewConfigBuilder.interactionDelay(interactionDelay: Duration?): ViewConfigBuilder =
    interactionDelay(interactionDelay?.toJavaDuration())
