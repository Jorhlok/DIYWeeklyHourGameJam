package net.jorhlok.diyweeklygamejam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.DungeonMaster

/**
 * Created by joshm on 6/18/2017.
 */
class Title(mapname: String,
            var MGR: MultiGfxRegister,
            var MAR: MultiAudioRegister) : DungeonMaster(mapname) {

    var statetime = 0f
    val animtime = 1f
    val offtime = 0.5f
    var pausetime = -1f
    val pauseperiod = 2f
    val muz = "4"

    override fun begin() {
        MGR.camera.setToOrtho(false,640f,360f)
        MGR.updateCam()
        MGR.getBufCam("main")?.setToOrtho(false,640f,360f)
        MGR.setBufScalar("main")
        val m = MAR.getMus(muz)
        m?.Generate()
        m?.play(true)
    }

    override fun prestep(deltaTime: Float) {

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            if (pausetime < 0) {
                pausetime = 0f
                MAR.playSFX("start2")
            }
        }
        if (pausetime >= 0) {
            pausetime += deltaTime
            if (pausetime >= pauseperiod) {
                Parent?.launchScript("testdm")
            }
        }
    }

    override fun end() {
        val m = MAR.getMus(muz)
        m?.stop()
    }

    override fun dispose() {
        val m = MAR.getMus(muz)
        m?.dispose()
    }

    override fun draw(deltatime: Float) {
        statetime += deltatime
        if (statetime >= animtime) statetime -= animtime
        MGR.startBuffer("main")
        MGR.clear(0.1f,0.1f,0.1f,1f)
        MGR.drawString("libmono","Furnish My House",320f,220.5f,2f,2f)
        MGR.drawString("libmono","RPG",320f,180.5f)
        if (statetime <= offtime) MGR.drawString("libmono","Press Start",320f,100.5f)
        MGR.stopBuffer()
        MGR.drawBuffer("main")
        MGR.flush()
    }
}