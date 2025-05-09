package io.github.theguardians.components

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.math.vec2

class PhysicComponent {
    val previousPosition = vec2()
    val impulse = vec2()
    lateinit var body: Body

    companion object {
        fun EntityCreateCfg.physicalComponentFromImage(
            world: World,
            image: Image,
            bodyType: BodyType,
            fixtureAction: BodyDefinition.(PhysicComponent, Float, Float) -> Unit
        ): PhysicComponent {
            val x = image.x
            val y = image.y
            val height = image.imageHeight
            val width = image.imageWidth

            return add {
                body = world.body(bodyType) {
                    position.set(x+width*0.5f, y+height*0.5f)
                    fixedRotation = true
                    allowSleep = false
                    this.fixtureAction(this@add, width, height)
                }

            }
        }

        class PhysicComponentListener: ComponentListener<PhysicComponent> {
            override fun onComponentAdded(entity: Entity, component: PhysicComponent) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicComponent) {
                val body = component.body
                body.world.destroyBody(body)
                body.userData = null
            }

        }
    }
}
