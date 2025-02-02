package io.github.theguardians

import io.github.theguardians.screens.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}
