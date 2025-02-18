package io.github.theguardians.components

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

data class SpawnConfiguration (
    val model: AnimationModel,
    val speedScaling: Float = 1f
)

const val DEFAULT_SPEED = 3f

data class SpawnComponent (
    var type: String = "",
    var location: Vector2 = vec2()
) {
}
