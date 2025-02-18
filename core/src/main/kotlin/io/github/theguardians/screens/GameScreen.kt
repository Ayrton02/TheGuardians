package io.github.theguardians.screens

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import io.github.theguardians.components.ImageComponent.Companion.ImageComponentListener
import io.github.theguardians.components.PhysicComponent.Companion.PhysicComponentListener
import io.github.theguardians.events.MapChangeEvent
import io.github.theguardians.input.PlayerKeyboardInputProcessor
import io.github.theguardians.systems.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2

class GameScreen: KtxScreen {
    private val stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/game.atlas")
    private var currentMap: TiledMap? = null
    private val physicsWorld = createWorld(vec2()).apply {
        autoClearForces = false
    }

    private val world: World = World {
        inject(stage)
        inject(textureAtlas)
        inject(physicsWorld)

        componentListener<ImageComponentListener>()
        componentListener<PhysicComponentListener>()

        system<EntitySpawnSystem>()
        system<MovementSystem>()
        system<PhysicSystem>()
        system<AnimationSystem>()
        system<RenderSystem>()
        system<DebugSystem>()
    }

    override fun show() {

        world.systems.forEach { system ->
            if (system is EventListener) {
                stage.addListener(system)
            }
        }

        currentMap = TmxMapLoader().load("map/map.tmx")
        stage.root.fire(MapChangeEvent(currentMap!!))

        PlayerKeyboardInputProcessor(world, world.mapper())
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        world.update(delta.coerceAtMost(0.25f))
    }

    override fun dispose() {
        stage.disposeSafely()
        textureAtlas.disposeSafely()
        world.dispose()
        physicsWorld.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
