package io.github.theguardians.systems

import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import io.github.theguardians.components.MovementComponent
import io.github.theguardians.components.PhysicComponent
import ktx.math.component1
import ktx.math.component2

@AllOf([MovementComponent::class, PhysicComponent::class])
class MovementSystem (
    private val movementComponents: ComponentMapper<MovementComponent>,
    private val physicComponents: ComponentMapper<PhysicComponent>
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val movementComponent = movementComponents[entity]
        val physicComponent = physicComponents[entity]
        val mass = physicComponent.body.mass
        val (velocityX, velocityY) = physicComponent.body.linearVelocity

        if (movementComponent.x == 0f && movementComponent.y == 0f) {
            physicComponent.impulse.set(
                mass * (0f - velocityX),
                mass * (0f - velocityY)
            )
            return
        }

        physicComponent.impulse.set(
            mass * (movementComponent.speed * movementComponent.x - velocityX),
            mass * (movementComponent.speed * movementComponent.y - velocityY)
        )
    }

}
