package io.github.theguardians.systems

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import io.github.theguardians.components.AnimationComponent
import io.github.theguardians.components.AnimationComponent.Companion.NO_ANIMATION
import io.github.theguardians.components.ImageComponent
import ktx.app.gdxError
import ktx.collections.map
import ktx.log.logger

@AllOf([AnimationComponent::class, ImageComponent::class])
class AnimationSystem(
    private val textureAtlas: TextureAtlas,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val imageComponents: ComponentMapper<ImageComponent>
): IteratingSystem() {

    private val cachedAnimation = mutableMapOf<String, Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        val animationComponent = animationComponents[entity]

        if (animationComponent.nextAnimation == NO_ANIMATION) {
            animationComponent.stateTime += deltaTime
        } else {
            animationComponent.animation = animation(animationComponent.nextAnimation)
            animationComponent.stateTime += 0f
            animationComponent.nextAnimation = NO_ANIMATION
        }

        animationComponent.animation.playMode = animationComponent.playMode
        imageComponents[entity].image.drawable = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
    }

    private fun animation(path: String): Animation<TextureRegionDrawable> {
        return cachedAnimation.getOrPut(path) {
            log.debug {"Animation being played '${path}'"}
            val regions = textureAtlas.findRegions(path)
            if (regions.isEmpty) {
                gdxError("No texture regions for path ${path}")
            }

            Animation(DEFAULT_FRAME_DURATION, regions.map { TextureRegionDrawable(it) })
        }
    }

    companion object {
        private val log = logger<AnimationSystem>()
        private val DEFAULT_FRAME_DURATION = 1 / 8f
    }
}
