@file:JvmName("ConfigExt")

package me.devnatan.inventoryframework

import kotlin.time.Duration

public fun ViewConfigBuilder.scheduleUpdate(interval: Duration): ViewConfigBuilder =
    scheduleUpdate(interval.inWholeSeconds / 20)