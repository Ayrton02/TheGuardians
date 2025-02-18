package io.github.theguardians.systems

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity
import io.github.theguardians.Main.Companion.UNIT_SCALE
import io.github.theguardians.components.ImageComponent
import io.github.theguardians.events.MapChangeEvent
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.tiled.forEachLayer

@AllOf([ImageComponent::class])
class RenderSystem(
    private val stage: Stage,
    private val imageComponents: ComponentMapper<ImageComponent>
):EventListener, IteratingSystem(
    comparator = compareEntity{ e1, e2 -> imageComponents[e1].compareTo(imageComponents[e2]) }
) {
    private val foreGroundLayer = mutableListOf<TiledMapTileLayer>()
    private val backGroundLayer = mutableListOf<TiledMapTileLayer>()
    private val mapRenderer = OrthogonalTiledMapRenderer(null, UNIT_SCALE, stage.batch)
    private val orthogonalCamera = stage.camera as OrthographicCamera

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(orthogonalCamera)
            if (backGroundLayer.isNotEmpty()) {
                stage.batch.use(orthogonalCamera.combined) {
                    backGroundLayer.forEach { mapRenderer.renderTileLayer(it) }
                }
            }

            act(deltaTime)
            draw()

            if (foreGroundLayer.isNotEmpty()) {
                stage.batch.use(orthogonalCamera.combined) {
                    foreGroundLayer.forEach { mapRenderer.renderTileLayer(it) }
                }
            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        imageComponents[entity].image.toFront()
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is MapChangeEvent -> {
                backGroundLayer.clear()
                foreGroundLayer.clear()

                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    if (layer.name.startsWith("fgd_")) {
                        foreGroundLayer.add(layer)
                    } else {
                        backGroundLayer.add(layer)
                    }
                }
                return true
            }
        }

        return false
    }

    override fun onDispose() {
        mapRenderer.disposeSafely()
    }
}
