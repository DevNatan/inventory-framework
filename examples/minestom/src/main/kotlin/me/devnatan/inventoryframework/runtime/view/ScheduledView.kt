package me.devnatan.inventoryframework.runtime.view

import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.runtime.ExampleUtil
import net.minestom.server.item.Material
import java.time.Duration

class ScheduledView : View() {

    val counter = mutableState(0)

    override fun onInit(config: ViewConfigBuilder) {
        config.cancelOnClick()
        config.size(3)
        config.title("Simple Pagination")
        config.layout(
            "         ",
            "    C    ",
            "B        ")
        config.scheduleUpdate(20)
    }

    override fun onFirstRender(render: RenderContext) {
        render.layoutSlot('C')
            .onRender {
                it.item = ExampleUtil.displayItem(Material.STONE, counter.increment(it).toString())
            }

        render.layoutSlot('B', ExampleUtil.displayItem(Material.PAPER, "Back"))
            .displayIf(Context::canBack)
            .onClick(SlotClickContext::back)
    }

}
