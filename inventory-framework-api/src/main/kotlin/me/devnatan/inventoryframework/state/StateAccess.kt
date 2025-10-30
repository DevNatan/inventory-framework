package me.devnatan.inventoryframework.state

import me.devnatan.inventoryframework.state.timer.TimerState
import kotlin.time.Duration

public fun StateAccess<*, *>.timerState(interval: Duration): TimerState = timerState(interval.inWholeSeconds / 20)
