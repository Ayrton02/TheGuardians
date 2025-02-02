package io.github.theguardians.systems

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity
import io.github.theguardians.components.ImageComponent

@AllOf([ImageComponent::class])
class RenderSystem(
    private val stage: Stage,
    private val imageComponents: ComponentMapper<ImageComponent>
): IteratingSystem(
    comparator = compareEntity{ e1, e2 -> imageComponents[e1].compareTo(imageComponents[e2]) }
) {

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()
            act(deltaTime)
            draw()
        }
    }

    override fun onTickEntity(entity: Entity) {
        imageComponents[entity].image.toFront()
    }
}
