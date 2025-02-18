package io.github.theguardians.systems

import com.badlogic.gdx.physics.box2d.World
import com.github.quillraven.fleks.*
import io.github.theguardians.components.ImageComponent
import io.github.theguardians.components.PhysicComponent
import ktx.math.component1
import ktx.math.component2

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem(
    private val physicWorld: World,
    private val imageComponents: ComponentMapper<ImageComponent>,
    private val physicComponents: ComponentMapper<PhysicComponent>
): IteratingSystem(interval = Fixed(1/60f)) {

    override fun onUpdate() {
        if (physicWorld.autoClearForces) {
            physicWorld.autoClearForces = false
        }
        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val physicComponent = physicComponents[entity]
        val imageComponent = imageComponents[entity]

        if (!physicComponent.impulse.isZero) {
            physicComponent.body.applyLinearImpulse(
                physicComponent.impulse,
                physicComponent.body.worldCenter,
                true
            )
            physicComponent.impulse.setZero()
        }

        val (bodyX, bodyY) = physicComponent.body.position
        imageComponent.image.run {
            setPosition(bodyX - width, bodyY - height)
        }

    }
}
