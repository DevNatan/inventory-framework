package me.devnatan.inventoryframework.runtime

import me.devnatan.inventoryframework.ViewFrame
import me.devnatan.inventoryframework.runtime.command.IFExampleCommand
import me.devnatan.inventoryframework.runtime.view.Failing
import me.devnatan.inventoryframework.runtime.view.SimplePagination
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerSkinInitEvent
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block

class SampleServer {

    init {
        val server = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        MinecraftServer.getCommandManager().register()

        // Create word filled with quartz blocks up to height 50
        val instance = instanceManager.createInstanceContainer()
        instance.setGenerator {
            it.modifier().fillHeight(it.absoluteStart().blockY(), 50, Block.QUARTZ_BLOCK)
        }
        instance.setChunkSupplier(::LightingChunk)

        val handler = MinecraftServer.getGlobalEventHandler()

        handler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
            event.spawningInstance = instance
            event.player.respawnPoint = Pos(0.0, 53.0, 0.0)
        }
        handler.addListener(PlayerSkinInitEvent::class.java) { event ->
            event.skin = PlayerSkin.fromUsername(event.player.username)
        }

        val viewFrame = ViewFrame.create()
            .with(Failing(), SimplePagination())
            .register()

        MinecraftServer.getCommandManager().register(IFExampleCommand(viewFrame))

        server.start("0.0.0.0", 25565)
    }

}

fun main() {
    SampleServer()
}
