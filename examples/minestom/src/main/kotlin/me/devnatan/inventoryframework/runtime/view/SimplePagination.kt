package me.devnatan.inventoryframework.runtime.view

import me.devnatan.inventoryframework.View
import me.devnatan.inventoryframework.ViewConfigBuilder
import me.devnatan.inventoryframework.component.MinestomIemComponentBuilder
import me.devnatan.inventoryframework.component.Pagination
import me.devnatan.inventoryframework.component.PaginationValueConsumer
import me.devnatan.inventoryframework.context.Context
import me.devnatan.inventoryframework.context.RenderContext
import me.devnatan.inventoryframework.context.SlotClickContext
import me.devnatan.inventoryframework.runtime.ExampleUtil.displayItem
import me.devnatan.inventoryframework.runtime.ExampleUtil.getRandomItems
import me.devnatan.inventoryframework.state.State
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.function.BooleanSupplier
import java.util.function.Supplier

class SimplePagination : View() {
    private val state: State<Pagination> = lazyPaginationState(
       {_ -> getRandomItems(123).toMutableList() },
        { _: Context, builder: MinestomIemComponentBuilder, index: Int, value: ItemStack ->
            builder.withItem(value)
            builder.onClick { ctx: SlotClickContext ->
                ctx.player.sendMessage(
                    "You clicked on item $index"
                )
            }
        })

    override fun onInit(config: ViewConfigBuilder) {
        config.cancelOnClick()
        config.size(3)
        config.title("Simple Pagination")
        config.layout("OOOOOOOOO", "OOOOOOOOO", "  P   N  ")
    }

    override fun onFirstRender(render: RenderContext) {
        val previousItem = displayItem(Material.ARROW, "Previous")
        val nextItem = displayItem(Material.ARROW, "Next")
        render.layoutSlot('P', previousItem)
            .displayIf(BooleanSupplier { state[render].canBack() })
            .updateOnStateChange(state)
            .onClick { ctx: SlotClickContext -> state[ctx].back() }
        render.layoutSlot('N', nextItem)
            .displayIf(BooleanSupplier { state[render].canAdvance() })
            .updateOnStateChange(state)
            .onClick { ctx: SlotClickContext -> state[ctx].advance() }
    }
}
