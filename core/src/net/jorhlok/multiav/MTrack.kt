package net.jorhlok.multiav

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music

/**

 * @author joshm
 */
class MTrack : Music.OnCompletionListener {
    //setup
    var name: String
        protected set
    protected var URIIntro: String? = null
    protected var URIBody: String

    //runtime
    protected var DataIntro: Music? = null
    protected var DataBody: Music? = null
    protected var GlobalVolume = 1f
    protected var State = 0

    constructor(key: String, uri: String) {
        name = key
        URIIntro = null
        URIBody = uri
    }

    constructor(key: String, intro: String, body: String) {
        name = key
        URIIntro = intro
        URIBody = body
    }

    fun setVolume(v: Float) {
        GlobalVolume = Math.max(v, 0f)
    }

    fun Generate() {
        dispose()
        try {
            DataIntro = Gdx.audio.newMusic(Gdx.files.internal(URIIntro))
        } catch (e: Exception) {
            DataIntro = null
        }

        try {
            DataBody = Gdx.audio.newMusic(Gdx.files.internal(URIBody))
        } catch (e: Exception) {
            DataBody = null
        }

    }

    fun dispose() {
        try {
            DataIntro!!.dispose()
        } catch (e: Exception) {
            //nothing
        }

        try {
            DataBody!!.dispose()
        } catch (e: Exception) {
            //nothing
        }

    }

    @JvmOverloads fun play(loop: Boolean = false) {
        stop()
        try {
            DataIntro!!.isLooping = false
            DataIntro!!.setOnCompletionListener(this)
            DataIntro!!.volume = GlobalVolume
            DataBody!!.volume = GlobalVolume
            if (loop)
                State = 3
            else
                State = 1
            if (DataBody != null) {
                DataBody!!.isLooping = loop
//                DataBody!!.stop()
//                DataBody!!.play()
//                DataBody!!.pause()
            }
            DataIntro!!.play()
        } catch (e: Exception) {
            playNoIntro(loop)
        }

    }

    @JvmOverloads fun playNoIntro(loop: Boolean = false) {
        stop()
        try {
            DataBody!!.isLooping = loop
            DataBody!!.volume = GlobalVolume
            DataBody!!.play()
        } catch (e: Exception) {
            //nothing
        }

    }

    fun stop() {
        try {
            DataIntro!!.stop()
        } catch (e: Exception) {
            //nothing
        }

        try {
            DataIntro!!.stop()
        } catch (e: Exception) {
            //nothing
        }

        State = 0
    }

    override fun onCompletion(m: Music) {
        try {
            when (State) {
                1 //single playthrough done with intro
                -> {
                    DataBody!!.play()
                    State = 2
                }
                2 //single playthrough done with loop
                -> State = 0
                3 //looping done with intro
                -> {
                    DataBody!!.play()
                    State = 4
                }
            }//nothing?
        } catch (e: Exception) {
            //nothing
        }

    }
}
