package net.jorhlok.multiav

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

/**
 * Created by joshm on 5/30/2017.
 */
class FrameRgb(
        var Name: String = "",
        var Image: String = "",
        var x: Int = 0,
        var y: Int = 0,
        var width: Int = 1,
        var height: Int = 1,
        var HFlip: Boolean = false,
        var VFlip: Boolean = false,
        var Rot90: Int = 0) {

    //generated
    var Tile = Sprite()

    fun Generate(map: Map<String, ImageRgb>) {
        var tex = map[Image]
        if (tex != null && tex.Tex != null) {
            Tile = Sprite(tex.Tex, x * tex.TWidth, y * tex.THeight, width * tex.TWidth, height * tex.THeight)
            Tile.setFlip(HFlip, VFlip)
            for (i in 0..Rot90 - 1) Tile.rotate90(false)
        }
    }

    fun draw(batch: Batch, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2? = null, col: Color = Color(1f,1f,1f,1f)) {
        var s = Sprite(Tile)
        if (center != null) s.setOrigin(center.x, center.y)
        else s.setOriginCenter()
        s.translate(x-s.originX, y-s.originY)
        s.setScale(sw, sh)
        s.rotate(rot)
        s.color = col
        try {
            s.draw(batch)
        } catch (e: Exception) {/*oh, well*/}
    }

    fun dispose() {
        Tile = Sprite()
    }
}