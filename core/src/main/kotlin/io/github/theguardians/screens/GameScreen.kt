package io.github.theguardians.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import io.github.theguardians.components.ImageComponent
import io.github.theguardians.components.ImageComponent.Companion.ImageComponentListener
import io.github.theguardians.systems.RenderSystem
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger

class GameScreen: KtxScreen {
    private val stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/TheGuardians.atlas")
    private val world: World = World {
        inject(stage)
        system<RenderSystem>()
        componentListener<ImageComponentListener>()
    }

    override fun show() {

        world.entity {
            add<ImageComponent> {
                image = Image(TextureRegion(textureAtlas.findRegion("player"), 0 ,0, 32, 32)).apply {
                    setSize(1f, 1f)
                    setScaling(Scaling.fill)
                }
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        stage.disposeSafely()
        textureAtlas.disposeSafely()
        world.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
