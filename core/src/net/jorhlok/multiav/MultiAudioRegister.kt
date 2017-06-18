package net.jorhlok.multiav

/**
 * Created by joshm on 6/2/2017.
 */
class MultiAudioRegister {
    private var SFX = HashMap<String, SEffect>()
    private var Mus = HashMap<String, MTrack>()

    fun newSFX(key: String, uri: String) {
        val s = SFX[key]
        s?.dispose()
        SFX.put(key, SEffect(key, uri))
    }

    fun newMusic(key: String, uri: String) {
        val m = Mus[key]
        m?.dispose()
        Mus.put(key, MTrack(key, uri))
    }

    /**
     * Two part music still has slight pause in the middle
     */
    fun newMusic(key: String, intro: String, body: String) {
        val m = Mus[key]
        m?.dispose()
        Mus.put(key, MTrack(key, intro, body))
    }

    fun Generate() {
        for (s in SFX.values) s.Generate()
    }

    fun dispose() {
        for (s in SFX.values)
            s.dispose()
        for (m in Mus.values)
            m.dispose()
    }

    fun getSFX(key: String): SEffect? {
        return SFX[key]
    }

    fun getMus(key: String): MTrack? {
        return Mus[key]
    }

    fun playSFX(key: String) {
        SFX[key]?.play()
    }

    fun setSFXVolume(v: Float) {
        for (s in SFX.values)
            s.setVolume(v)
    }

    fun setMusVolume(v: Float) {
        for (m in Mus.values)
            m.setVolume(v)
    }

    fun killMusic() {
        for (m in Mus.values) {
            m.stop()
            m.dispose()
        }
    }
}