package net.jorhlok.diyweeklygamejam

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.DungeonMaster
import net.jorhlok.oops.LabelledObject

/**
 * Created by joshm on 6/18/2017.
 */
class DM(mapname: String,
         var MGR: MultiGfxRegister,
         var MAR: MultiAudioRegister) : DungeonMaster(mapname) {
    var Player: Plar? = null
    var muz = "2"
    var renderer: OrthogonalTiledMapRenderer? = null

    override fun begin() {
        var exiting = "Begin"
        val str = Parent?.GlobalData?.get("Exiting")?.label
        if (str != null) exiting = str
        Parent?.GlobalData?.put("Exiting", LabelledObject(MapName))
        val PlyrStart = Vector2(20f,2f)

//        val LyrTileObj = Level!!.layers["TileObjects"] as TiledMapTileLayer?
//        if (LyrTileObj != null)
//            for (y in 0..LyrTileObj.height-1)
//                for (x in 0..LyrTileObj.width-1) {
//    //                val obj = LyrTileObj.getCell(x,y)
//    //                if (obj != null && obj.tile != null) when (obj.tile.id) {
//
//    //                }
//                }

        val LyrNonTile = Level!!.layers["NonTiles"]
        if (LyrNonTile != null)
            for (o in LyrNonTile.objects) {
                if (o is RectangleMapObject) {
                    val type = o.properties["type"]
                    if (type != null) when {
                        type.toString().startsWith("Transition") ->
                            Living.add(Transition(o.name, o.rectangle, this, MGR, MAR))
                        type.toString().startsWith("Entrance") ->
                            if (type.toString().endsWith(exiting)) PlyrStart.set(o.rectangle.x/16,o.rectangle.y/16)
                        type.toString().startsWith("Spawner") ->
                                Living.add(Spawner(o.name, Rectangle(o.rectangle.x/16,o.rectangle.y/16,o.rectangle.width/16,o.rectangle.height/16),this,MGR,MAR))
                    }
                }
            }


        MGR.camera.setToOrtho(false,640f,360f)
        MGR.updateCam()
        MGR.setBufScalar("main",1/16f)
        val cam = MGR.getBufCam("main")!!
        cam.setToOrtho(false,640/16f,360/16f)

        renderer = OrthogonalTiledMapRenderer(Level,1/16f)
        renderer?.setView(cam)

        val m = MAR.getMus(muz)
        if (m != null) {
            m.Generate()
            m.play(true)
        }

        Player = Plar(MGR,MAR)
        Player!!.Name = "Player"
        Player!!.Position.set(PlyrStart)
        Living.add(Player!!)
    }

    override fun prestep(deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)
                || Gdx.input.isKeyPressed(Input.Keys.W))
            Player!!.Mailbox.add(LabelledObject("CtrlUp",true))
        else
            Player!!.Mailbox.add(LabelledObject("CtrlUp",false))

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            Player!!.Mailbox.add(LabelledObject("CtrlRt",true))
        else
            Player!!.Mailbox.add(LabelledObject("CtrlRt",false))

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            Player!!.Mailbox.add(LabelledObject("CtrlLf",true))
        else
            Player!!.Mailbox.add(LabelledObject("CtrlLf",false))

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            Player!!.Mailbox.add(LabelledObject("CtrlDn",true))
        else
            Player!!.Mailbox.add(LabelledObject("CtrlDn",false))
    }

    override fun end() {
        val m = MAR.getMus(muz)
        if (m != null) {
            m.stop()
            m.dispose()
        }
        Living.clear()
        Player = null
        renderer?.dispose()
    }

    override fun draw(deltatime: Float) {

        MGR.startBuffer("main")
        if (renderer?.batch != null && renderer!!.batch.isDrawing) renderer!!.batch.end()
        renderer?.batch?.begin()
        if (Level!!.layers["Background"] != null) renderer?.renderTileLayer(Level!!.layers["Background"] as TiledMapTileLayer)
        if (Level!!.layers["Foreground"] != null) renderer?.renderTileLayer(Level!!.layers["Foreground"] as TiledMapTileLayer)
        renderer?.batch?.end()
        for (e in Living) {
            e.draw(deltatime)
        }
        MGR.stopBuffer()
        MGR.drawBuffer("main")
        MGR.flush()
    }
}