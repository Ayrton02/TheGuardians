package io.github.theguardians.systems

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import io.github.theguardians.Main.Companion.UNIT_SCALE
import io.github.theguardians.components.*
import io.github.theguardians.components.PhysicComponent.Companion.physicalComponentFromImage
import io.github.theguardians.events.MapChangeEvent
import ktx.app.gdxError
import ktx.box2d.box
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y

@AllOf([SpawnComponent::class])
class EntitySpawnSystem (
    private val physicWorld: World,
    private val spawnComponents: ComponentMapper<SpawnComponent>
): EventListener, IteratingSystem() {
    private val cachedConfigs = mutableMapOf<String, SpawnConfiguration>()

    private fun spawnConfiguration(type: String): SpawnConfiguration = cachedConfigs.getOrPut(type) {
        when (type) {
            "Player" -> SpawnConfiguration(AnimationModel.PLAYER)
            else -> gdxError("No spawn configuration in $type")
        }
    }

    override fun onTickEntity(entity: Entity) {
        with(spawnComponents[entity]) {
            val configuration = spawnConfiguration(type)

            world.entity {

                val imageComponent = add<ImageComponent> {
                    image = Image().apply {
                        setPosition(location.x, location.y)
                        setSize(1f, 1f)
                        setScaling(Scaling.fill)
                    }
                }
                add<AnimationComponent> {
                    nextAnimation(configuration.model, AnimationType.FRONT)
                }

                if (configuration.speedScaling > 0f) {
                    add<MovementComponent> {
                        speed = DEFAULT_SPEED * configuration.speedScaling
                    }
                }

                if(type == "Player") {
                    add<PlayerComponent>()
                    add<CameraLockComponent>()
                }

                physicalComponentFromImage(physicWorld, imageComponent.image, BodyDef.BodyType.DynamicBody) {
                    physicComponent, width, heigth -> box(width, heigth) {
                        isSensor = false
                    }
                }
            }
        }
        world.remove(entity)
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                entityLayer.objects.forEach { o ->
                    val type = o.type ?: gdxError("object $o does not have a type")

                    world.entity {
                        add<SpawnComponent> {
                            this.type = type
                            this.location.set(o.x * UNIT_SCALE, o.y * UNIT_SCALE)
                        }
                    }
                }

                return true
            }
        }

        return false
    }

}
