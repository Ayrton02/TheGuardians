package io.github.theguardians.components

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import ktx.math.vec2

data class SpawnConfiguration (
    val model: AnimationModel,
    val speedScaling: Float = 1f,
    val physicScaling: Vector2 = vec2(1f, 1f),
    val physicOffSet: Vector2 = vec2(0f, 0f),
    var bodyType: BodyType = BodyType.DynamicBody
)

const val DEFAULT_SPEED = 3f

data class SpawnComponent (
    var type: String = "",
    var location: Vector2 = vec2()
) {
}
