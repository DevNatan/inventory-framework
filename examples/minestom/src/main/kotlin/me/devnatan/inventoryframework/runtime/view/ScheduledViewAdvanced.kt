package me.devnatan.inventoryframework.runtime.view

import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.runtime.ExampleUtil
import me.devnatan.inventoryframework.state.timerState
import net.minestom.server.item.Material
import kotlin.time.Duration.Companion.seconds

class ScheduledViewAdvanced : View() {
    val counter = mutableState(0)
    val timer = timerState(1.seconds)

    override fun onInit(config: ViewConfigBuilder): Unit =
        with(config) {
            cancelOnClick()
            size(3)
            title("Simple Pagination")
            layout("         ", "    C    ", "B        ")
            scheduleUpdate(timer)
        }

    override fun onFirstRender(render: RenderContext) {
        render.layoutSlot('C').onRender {
            it.item = ExampleUtil.displayItem(Material.STONE, counter.increment(it).toString())
        }

        render
            .layoutSlot('B', ExampleUtil.displayItem(Material.PAPER, "Back"))
            .displayIf(Context::canBack)
            .onClick(SlotClickContext::back)
    }
}
