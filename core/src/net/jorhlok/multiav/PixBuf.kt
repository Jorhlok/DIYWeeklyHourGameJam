package net.jorhlok.multiav

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer

/**
 * Created by joshm on 6/1/2017.
 */
class PixBuf (width: Int,
              height: Int,
              camwidth: Float,
              camheight: Float,
              var scalar: Float = 1f,
              format: Pixmap.Format = Pixmap.Format.RGBA8888) {
    var drawing = false
    var fb = FrameBuffer(format,width,height,false)
    var tex = TextureRegion(fb.colorBufferTexture)
    var cam = OrthographicCamera()

    init {
        cam.setToOrtho(false,camwidth,camheight)
        cam.update()
        fb.colorBufferTexture?.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        tex.flip(false,true)
    }

    fun begin() {
        if (!drawing) {
            cam.update()
            fb.begin()
            drawing = true
        }
    }

    fun end() {
        if (drawing) {
            fb.end()
            drawing = false
        }
    }

    fun dispose() {
        end()
        fb.dispose()
    }
}