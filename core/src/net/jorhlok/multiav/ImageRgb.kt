package net.jorhlok.multiav

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

/**
 * Created by joshm on 5/30/2017.
 */
class ImageRgb(
        var Name: String = "",
        var URI: String = "",
        var TWidth: Int = 1,
        var THeight:Int = 1) {

    var Tex: Texture? = null

    fun Generate() {
        Tex = Texture(Gdx.files.internal(URI))
    }

    fun dispose() {
        Tex?.dispose()
    }

}