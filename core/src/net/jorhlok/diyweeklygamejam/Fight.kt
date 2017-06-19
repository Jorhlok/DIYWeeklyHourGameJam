package net.jorhlok.diyweeklygamejam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.DungeonMaster

/**
 * Created by joshm on 6/18/2017.
 */
class Fight(mapname: String,
            var MGR: MultiGfxRegister,
            var MAR: MultiAudioRegister) : DungeonMaster(mapname) {
    var muz = "3"
    var renderer: OrthogonalTiledMapRenderer? = null
    var buttonold = false
    var statetime = 0f
    var text = ""
    var drawtext = ""
    var color = Color(1f,1f,1f,1f)
    var state = 0
    var damage = 0
    var damagegoal = 1
    var Mon = "Skelly"

    override fun begin() {
        val str = Parent?.GlobalData?.get("Monster")?.label
        if (str != null) Mon = str

        damage = 0
        damagegoal = (Math.random()*100).toInt()

        text = "Ambushed by a $Mon."
        drawtext = ""
        color = Color(1f,1f,1f,1f)
        statetime = 0f
        state = 0
        buttonold = false

        MGR.camera.setToOrtho(false,640f,360f)
        MGR.updateCam()
        MGR.setBufScalar("battle",1/16f)
        val cam = MGR.getBufCam("battle")!!
        cam.setToOrtho(false,640/16f,360/16f)

        renderer = OrthogonalTiledMapRenderer(Level,1/16f)
        renderer?.setView(cam)

        val m = MAR.getMus(muz)
        if (m != null) {
            m.Generate()
            m.play(true)
        }
    }

    override fun prestep(deltaTime: Float) {
        var button = false
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyPressed(Input.Keys.ENTER))
            button = true

        //do thing
        if (text.length > drawtext.length) {
            var textlen = (statetime*8).toInt()
            if (textlen >= text.length) textlen = text.length-1
            drawtext = text.substring(0..textlen)
            statetime += deltaTime*3 //run faster
        }
        else if (button && !buttonold) {
            val num = (Math.random()*50).toInt()
            when {
                state >= 0 -> {
                    if (state == 0)
                        state = Math.round(Math.random()*2+1).toInt()
                    if (damage >= damagegoal) {
                        text = "You defeated the dreaded $Mon! You got an item."
                        color = Color(1f,1f,1f,1f)
                        MAR.getMus(muz)?.stop()
                        MAR.getMus(muz)?.dispose()
                        MAR.playSFX("start1")
                        state = -2
                    } else if ((state/2)*2==state) { //even
                        damage += num
                        text = "You struck the $Mon for $num damage."
                        MAR.playSFX("beep")
                        color = Color(0f,1f,0f,1f)
                    } else {
                        text = "The $Mon struck you for $num damage."
                        MAR.playSFX("boop")
                        color = Color(1f,0f,0f,1f)
                    }
                    ++state
                }
                else -> {
                    val str = Parent?.GlobalData?.get("Exiting")?.label
                    if (str != null) Parent?.launchScript(str)
                }
            }
            drawtext = ""
            statetime = 0f
        }

        buttonold = button
        statetime += deltaTime
    }

    override fun end() {
        val m = MAR.getMus(muz)
        if (m != null) {
            m.stop()
            m.dispose()
        }
        renderer?.dispose()
        renderer = null
    }

    override fun draw(deltatime: Float) {

        MGR.startBuffer("battle")
        MGR.blank()
        if (renderer?.batch != null && renderer!!.batch.isDrawing) renderer!!.batch.end()
        renderer?.batch?.begin()
        if (Level!!.layers["Background"] != null) renderer?.renderTileLayer(Level!!.layers["Background"] as TiledMapTileLayer)
        if (Level!!.layers["Foreground"] != null) renderer?.renderTileLayer(Level!!.layers["Foreground"] as TiledMapTileLayer)
        renderer?.batch?.end()
        //drawtext
        MGR.drawString("libmono",drawtext,1f,1f,1f,1f,0f,Vector2(),color)
        MGR.drawRgb("spin",statetime,39f,1f)
        MGR.stopBuffer()
        MGR.drawBuffer("main")
        MGR.drawBuffer("battle")
        MGR.flush()
    }
}