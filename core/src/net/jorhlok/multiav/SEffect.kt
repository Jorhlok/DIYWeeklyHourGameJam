package net.jorhlok.multiav

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound

/**

 * @author joshm
 */
class SEffect(key: String, protected var URI: String) {
    //setup
    var name: String
        protected set
    protected var GlobalVolume = 1f

    //runtime
    protected var Data: Sound? = null

    init {
        name = key
    }

    fun Generate() {
        Data = Gdx.audio.newSound(Gdx.files.internal(URI))
    }

    fun setVolume(v: Float) {
        GlobalVolume = Math.max(v, 0f)
    }

    fun play(loop: Boolean): Long {
        return play(1f, 1f, 0f, loop)
    }

    fun play(volume: Float, loop: Boolean): Long {
        return play(volume, 1f, 0f, loop)
    }

    fun play(volume: Float, speed: Float, loop: Boolean): Long {
        return play(volume, speed, 0f, loop)
    }

    @JvmOverloads fun play(volume: Float = 1f, speed: Float = 1f, pan: Float = 0f, loop: Boolean = false): Long {
        try {
            val id = Data!!.play(volume * GlobalVolume, speed, pan)
            Data?.setLooping(id, loop)
            return id
        } catch (e: Exception) {
            return -1
        }

    }

    fun stop() {
        try {
            Data?.stop()
        } catch (e: Exception) {
            //nothing
        }

    }

    fun stop(id: Long) {
        try {
            Data?.stop(id)
        } catch (e: Exception) {
            //nothing
        }

    }

    fun dispose() {
        try {
            Data?.dispose()
        } catch (e: Exception) {
            //nothing
        }

    }
}
