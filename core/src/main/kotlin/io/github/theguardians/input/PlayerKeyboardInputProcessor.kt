package io.github.theguardians.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import io.github.theguardians.components.MovementComponent
import io.github.theguardians.components.PlayerComponent
import ktx.app.KtxInputAdapter

class PlayerKeyboardInputProcessor (
    world: World,
    private val movementComponents: ComponentMapper<MovementComponent>
): KtxInputAdapter {
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))
    private var playerX = 0f
    private var playerY = 0f

    private fun Int.isMovementKey(): Boolean {
        return this == UP || this == DOWN || this == LEFT || this == RIGHT
    }

    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with(movementComponents[player]) {
                x = playerX
                y = playerY
            }
        }
    }

    init {
        Gdx.input.inputProcessor = this
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode.isMovementKey()) {
            when (keycode) {
                UP -> {
                    this.playerY = 1f
                    this.playerX = 0f
                }

                DOWN -> {
                    this.playerY = -1f
                    this.playerX = 0f
                }

                LEFT -> {
                    this.playerX = -1f
                    this.playerY = 0f
                }

                RIGHT -> {
                    this.playerX = 1f
                    this.playerY = 0f
                }
            }
            updatePlayerMovement()
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode.isMovementKey()) {
            when(keycode) {
                UP -> this.playerY = 0f
                DOWN -> this.playerY = 0f
                LEFT -> this.playerX = 0f
                RIGHT -> this.playerX = 0f
            }
            updatePlayerMovement()
            return true
        }
        return false
    }
}
