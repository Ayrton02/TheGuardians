package io.github.theguardians.screens

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import io.github.theguardians.components.AnimationComponent
import io.github.theguardians.components.AnimationModel
import io.github.theguardians.components.AnimationType
import io.github.theguardians.components.ImageComponent
import io.github.theguardians.components.ImageComponent.Companion.ImageComponentListener
import io.github.theguardians.systems.AnimationSystem
import io.github.theguardians.systems.RenderSystem
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger

class GameScreen: KtxScreen {
    private val stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/game.atlas")
    private val world: World = World {
        inject(stage)
        inject(textureAtlas)

        componentListener<ImageComponentListener>()

        system<AnimationSystem>()
        system<RenderSystem>()
    }

    override fun show() {

        world.entity {
            add<ImageComponent> {
                image = Image().apply {
                    setSize(2f, 2f)
                    setScaling(Scaling.fill)
                }
            }
            add<AnimationComponent> {
                nextAnimation(AnimationModel.PLAYER, AnimationType.FRONT)
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
