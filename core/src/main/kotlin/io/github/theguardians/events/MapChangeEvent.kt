package io.github.theguardians.events

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.scenes.scene2d.Event

data class MapChangeEvent(val map: TiledMap): Event() {
}
