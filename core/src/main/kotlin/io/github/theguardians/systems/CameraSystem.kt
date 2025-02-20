package io.github.theguardians.systems;

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf;
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import io.github.theguardians.components.CameraLockComponent
import io.github.theguardians.components.ImageComponent
import io.github.theguardians.events.MapChangeEvent
import ktx.tiled.height
import ktx.tiled.width

@AllOf([CameraLockComponent::class, ImageComponent::class])
class CameraSystem(
    stage: Stage,
    private val imageComponents: ComponentMapper<ImageComponent>
):EventListener, IteratingSystem() {
    private val camera = stage.camera
    private var maxWidth = 0f
    private var maxHeight = 0f

    override fun onTickEntity(entity: Entity) {
        val component = imageComponents[entity]
        val viewWidth = camera.viewportWidth * 0.5f
        val viewHeight = camera.viewportHeight * 0.5f

        camera.position.set(
            component.image.x.coerceIn(viewWidth, maxWidth - viewWidth),
            component.image.y.coerceIn(viewHeight, maxHeight - viewHeight),
            camera.position.z
        )
    }

    override fun handle(event: Event): Boolean {
        if (event is MapChangeEvent) {
            maxWidth = event.map.width.toFloat()
            maxHeight = event.map.height.toFloat()
            return true
        }

        return false
    }


}
