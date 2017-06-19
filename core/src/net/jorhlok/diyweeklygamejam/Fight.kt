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
    var text = "Ambushed by a "
    var color = Color(1f,1f,1f,1f)
    override fun begin() {
        var Mon = "Skelly"
        val str = Parent?.GlobalData?.get("Monster")?.label
        if (str != null) Mon = str

        text += Mon

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
        MGR.drawString("libmono",text,1f,1f,1f,1f,0f,Vector2(),color)
        MGR.drawRgb("spin",statetime,39f,1f)
        MGR.stopBuffer()
        MGR.drawBuffer("main")
        MGR.drawBuffer("battle")
        MGR.flush()
    }
}