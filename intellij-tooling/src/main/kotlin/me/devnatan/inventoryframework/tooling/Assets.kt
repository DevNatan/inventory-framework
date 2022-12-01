package me.devnatan.inventoryframework.tooling

import java.io.File

object Assets {

    private const val root = "/assets"
    const val sprites = "sprites"

    operator fun invoke(name: String): String {
        return "$root/$name"
    }

}

fun sprite(name: String) = Assets(Assets.sprites + "/" + name)