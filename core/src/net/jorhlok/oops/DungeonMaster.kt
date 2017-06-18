package net.jorhlok.oops

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import java.util.LinkedList
import java.util.Queue

/**
 * The master script in a room.
 * @author Jorhlok
 */
open class DungeonMaster(
        var MapName: String,
        var StrTerrain: String = "Terrain") {

    interface OOPS {
        val GlobalData: MutableMap<String, LabelledObject>
        fun launchScript(key: String)
    }

    //runtime
    var Parent: OOPS? = null
    var Level: TiledMap? = null
    var LyrTerrain: TiledMapTileLayer? = null
    var Living: LinkedList<Entity> = LinkedList()
    var TileW = 1f
    var TileH = 1f

    fun create(maps: Map<String, TiledMap>, parent: OOPS) {
        Parent = parent
        Level = maps[MapName]
        LyrTerrain = Level?.layers?.get(StrTerrain) as TiledMapTileLayer?
    }

    open fun begin() {}
    open fun end() {}
    open fun dispose() {}

    open fun prestep(deltaTime: Float) {}
    open fun poststep(deltaTime: Float) {}

    fun update(deltatime: Float) {
        prestep(deltatime)
        for (e in Living) e.prestep(deltatime)
        for (e in Living) e.doSimplePhysics(deltatime)

        //check for collisions with entities/tiles if they wish and engage mid-step if they need it
        val q: Queue<LabelledObject> = LinkedList()
        val iter = Living.listIterator()
        while (iter.hasNext()) { //everybody handshake  sum(1..n)  n(n-1)/2  O(n^2)
            val obj = iter.next()
            if (obj.CollEntities) {
                Living.listIterator(iter.nextIndex()).forEachRemaining { e ->
                    if (e.CollEntities && e.AOI.overlaps(obj.AOI)) {
                        if (obj.CollEntWhite == null && obj.CollEntGray == null ||
                                obj.CollEntWhite != null && e.Type in obj.CollEntWhite!! ||
                                    obj.CollEntGray != null && e.Type in obj.CollEntGray!!
                                        && obj.checkCollEntity(deltatime,e)) {
                            obj.Mailbox.add(LabelledObject("CollEnt/${e.Type}", e))
                            if (obj.CollEntAsTile != null && e.Type in obj.CollEntAsTile!!)
                                obj.CollQueue.add(LabelledObject("Ent/${e.Type}",e))
                        }

                        if (e.CollEntWhite == null && e.CollEntGray == null ||
                                e.CollEntWhite != null && obj.Type in e.CollEntWhite!! ||
                                    e.CollEntGray != null && obj.Type in e.CollEntGray!!
                                        && e.checkCollEntity(deltatime,obj)) {
                            e.Mailbox.add(LabelledObject("CollEnt/${obj.Type}", obj))
                            if (e.CollEntAsTile != null && obj.Type in e.CollEntAsTile!!)
                                e.CollQueue.add(LabelledObject("Ent/${obj.Type}",obj))
                        }
                    }
                }
            }
            if (obj.CollTiles) {
                CollectTiles(q, obj.AOI)
                for (o in q) {
                    val tile = o.obj as Array<*>
                    val c = tile[0] as TiledMapTileLayer.Cell
                    val r = tile[1] as Rectangle
                    if (obj.CollTileWhite == null && obj.CollTileGray == null ||
                            obj.CollTileWhite != null && c.tile.id in obj.CollTileWhite!! ||
                                obj.CollTileGray != null && c.tile.id in obj.CollTileGray!! &&
                                    obj.checkCollTile(deltatime,c,r.x.toInt(),r.y.toInt()))
                        obj.CollQueue.add(LabelledObject("Tile/${c.tile.id}",r))
                }
                q.clear()
            }
        }

        for (e in Living) e.collideWithTiles()
        for (e in Living) e.poststep(deltatime)
        poststep(deltatime)
        for (e in Living) {
            e.Mailbox.clear()
            e.CollQueue.clear()
        }
    }

    open fun draw(deltatime: Float) {

    }

    fun CollectTiles(q: Queue<LabelledObject>, aoi: Rectangle) {
        val x1 = Math.floor(aoi.x.toDouble()).toInt()
        val y1 = Math.floor(aoi.y.toDouble()).toInt()
        val x2 = Math.ceil((aoi.x + aoi.width).toDouble()).toInt()
        val y2 = Math.ceil((aoi.y + aoi.height).toDouble()).toInt()
        //collide with tiles
        for (y in y1..y2) {
            for (x in x1..x2) {
                val c = LyrTerrain?.getCell(x, y)
                if (c != null && c.tile != null) //TODO: px->world auto?
                    q.add(LabelledObject("Tile",arrayOf(c,Rectangle(x.toFloat(),y.toFloat(),TileW,TileH)))) //LyrTerrain!!.tileWidth,LyrTerrain!!.tileHeight))))
            }
        }
    }

    fun keepPointInBox(pt: Vector2, box: Rectangle): Vector2 {
        val ret = Vector2(pt)
        if (!box.contains(pt)) {
            if (pt.x < box.x) ret.x = box.x
            else if (pt.x > box.x + box.width) ret.x = box.x + box.width
            if (pt.y < box.y) ret.y = box.y
            else if (pt.y > box.y + box.height) ret.y = box.y + box.height
        }
        return ret
    }

}
