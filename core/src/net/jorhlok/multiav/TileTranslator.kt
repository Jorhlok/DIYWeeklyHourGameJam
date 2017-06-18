package net.jorhlok.multiav

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2

/**
 * Created by joshm on 6/2/2017.
 */
class TileTranslator(
        val iColor: Boolean = false,
        val colorCode: Int = 0, //if iColor then is color offset else is a color in integer format
        val rotate: Boolean = false,
        val anim: String,
        val animH: String = "",
        val animV: String = "",
        val animHV: String = "") {

    fun draw(mgr: MultiGfxRegister, cell: TiledMapTileLayer.Cell, stateTime: Float, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f) {
        var thisanim = anim
        if (animH != "" && cell.flipHorizontally && !cell.flipVertically ) thisanim = animH
        if (animV != "" && !cell.flipHorizontally && cell.flipVertically ) thisanim = animV
        if (animHV != "" && cell.flipHorizontally && cell.flipVertically ) thisanim = animHV
        var rotation = rot
        if (rotate) when (cell.rotation) {
            TiledMapTileLayer.Cell.ROTATE_90 -> rotation += 90
            TiledMapTileLayer.Cell.ROTATE_180 -> rotation += 180
            TiledMapTileLayer.Cell.ROTATE_270 -> rotation += 270
        }
        if (iColor) mgr.drawPal(thisanim,colorCode.toShort(),stateTime,x,y,sw,sh,rotation,Vector2())
        else mgr.drawRgb(thisanim,stateTime,x,y,sw,sh,rotation, Vector2(), Color(colorCode))
    }
}