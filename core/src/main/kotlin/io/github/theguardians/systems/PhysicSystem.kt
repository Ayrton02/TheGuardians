package io.github.theguardians.systems

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
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
): ContactListener, IteratingSystem(interval = Fixed(1/60f)) {

    init {
        physicWorld.setContactListener(this)
    }

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

        physicComponent.previousPosition.set(
            physicComponent.body.position
        )

        if (!physicComponent.impulse.isZero) {
            physicComponent.body.applyLinearImpulse(
                physicComponent.impulse,
                physicComponent.body.worldCenter,
                true
            )
            physicComponent.impulse.setZero()
        }
    }

    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val physicComponent = physicComponents[entity]
        val imageComponent = imageComponents[entity]

        val (previousX, previousY) = physicComponent.previousPosition
        val (bodyX, bodyY) = physicComponent.body.position
        imageComponent.image.run {
            setPosition(
                MathUtils.lerp(previousX, bodyX, alpha) - width,
                MathUtils.lerp(previousY, bodyY, alpha) - height
            )
        }
    }

    override fun beginContact(p0: Contact?) {
        TODO("Not yet implemented")
    }

    override fun endContact(p0: Contact?) {
        TODO("Not yet implemented")
    }

    override fun preSolve(contact: Contact, manifold: Manifold) {
        contact.isEnabled =
            (contact.fixtureA.body.type == StaticBody && contact.fixtureB.body.type == DynamicBody) ||
            (contact.fixtureB.body.type == StaticBody && contact.fixtureA.body.type == DynamicBody)
    }

    override fun postSolve(p0: Contact?, p1: ContactImpulse?) = Unit
}
