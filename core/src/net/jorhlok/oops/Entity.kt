package net.jorhlok.oops

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import java.util.LinkedList
import java.util.Queue

/**
 * Some sort of entity. May or may not exist in worldspace.
 * @author Jorhlok
 */
open class Entity {
    //properties
    var Name = ""
    var Type = "Entity"
    var AABB = Rectangle()
    var Physics = false
    var CollEntities = false
    var CollEntWhite: Set<String>? = null
    var CollEntGray: Set<String>? = null
    var CollEntAsTile: Set<String>? = null
    var CollTiles = false
    var CollTileWhite: Set<Int>? = null
    var CollTileGray: Set<Int>? = null
    var Tolerance = Vector2() //the extra space of each axis for tile collisions
    var VerticalFirst = true //order each axis is checked

    //runtime
    var Parent: DungeonMaster? = null
    var Mailbox: Queue<LabelledObject> = LinkedList()
    var CollQueue: Queue<LabelledObject> = LinkedList()
    var Position = Vector2() //in world space
    var Velocity = Vector2() //world units per second
    var Friction = Vector2() //clamps velocity to zero
    var PrePosition = Vector2() //before simple physics
    var PreVelocity = Vector2()
    var AOI = Rectangle()

    open fun begin() {}
    open fun prestep(deltatime: Float) {}
    open fun checkCollEntity(deltatime: Float, e: Entity) = false
    open fun checkCollTile(deltatime: Float, c: TiledMapTileLayer.Cell, x: Int, y: Int) = false
    open fun poststep(deltatime: Float) {}
    open fun draw(deltatime: Float) {}
    open fun end() {}

    fun doSimplePhysics(deltatime: Float) {
        AOI.set(mkRect())
        PrePosition.set(Position)
        if (Physics) {
            if (Physics) {
                PreVelocity.set(Velocity)
                //apply velocity
                Position.add(Velocity.cpy().scl(deltatime))
                //apply friction
                if (Velocity.x > 0) {
                    Velocity.x -= Friction.x * deltatime
                    if (Velocity.x < 0) Velocity.x = 0f
                } else if (Velocity.x < 0) {
                    Velocity.x += Friction.x * deltatime
                    if (Velocity.x > 0) Velocity.x = 0f
                }
                if (Velocity.y > 0) {
                    Velocity.y -= Friction.y * deltatime
                    if (Velocity.y < 0) Velocity.y = 0f
                } else if (Velocity.y < 0) {
                    Velocity.y += Friction.y * deltatime
                    if (Velocity.y > 0) Velocity.y = 0f
                }
            }
        }
        AOI.merge(mkRect())
    }

    fun collideWithTiles() {
        if (Physics && CollQueue.isNotEmpty()) {
            val len = CollQueue.size
            PrePosition.set(Position)

            //check tiles for collisions and make adjustments
            for (j in 0..1) {
                if (VerticalFirst && j == 0 || !VerticalFirst && j == 1) {
                    for (i in 0..len-1) {
                        var r = Rectangle()
                        val t = CollQueue.poll()
                        CollQueue.add(t)
                        when {
                            t.label.startsWith("Ent") -> r = (t.obj as Entity).mkRect()
                            t.label.startsWith("Tile") -> r = t.obj as Rectangle
                        }
                        val vRect = mkVRect()
                        if (r.overlaps(vRect)) {
                            if (vRect.y+vRect.height/2 >= r.y+r.height/2) { //falling down, shunt up
                                Position.add(0f, r.y + r.height - Position.y - AABB.y)
                                Mailbox.add((LabelledObject("BumpUp/${t.label}",r)))
                            }
                            else { //rising up, shunt down
                                Position.sub(0f, Position.y + AABB.y + AABB.height - r.y)
                                Mailbox.add((LabelledObject("BumpDn/${t.label}",r)))
                            }
                            Velocity.y = 0f
                        }
                    }
                } else {
                    for (i in 0..len-1) {
                        var r = Rectangle()
                        val t = CollQueue.poll()
                        CollQueue.add(t)
                        when {
                            t.label.startsWith("Ent") -> r = (t.obj as Entity).mkRect()
                            t.label.startsWith("Tile") -> r = t.obj as Rectangle
                        }
                        val hRect = mkHRect()
                        if (r.overlaps(hRect)) {
                            if (hRect.x+hRect.width/2 >= r.x+r.width/2) { //moving left, shunt right
                                Position.add(r.x + r.width - Position.x - AABB.x, 0f)
                                Mailbox.add((LabelledObject("BumpRt/${t.label}")))
                            }
                            else { //moving right, shunt left
                                Position.sub(Position.x + AABB.x + AABB.width - r.x, 0f)
                                Mailbox.add((LabelledObject("BumpLf/${t.label}")))
                            }
                            Velocity.x = 0f
                        }
                    }
                }
            }
        }
    }

    fun mkRect() = mkRect(AABB,Position)

    fun mkVRect() = mkVRect(mkRect(),Tolerance)

    fun mkHRect() = mkHRect(mkRect(),Tolerance)

    fun mkRect(aabb: Rectangle, pos: Vector2) = Rectangle(aabb.x+pos.x,aabb.y+pos.y,aabb.width,aabb.height)

    fun mkVRect(aabb: Rectangle, tol: Vector2) = Rectangle(aabb.x + (aabb.width - aabb.width * tol.x) / 2,
                                                            aabb.y,
                                                            aabb.width * tol.x,
                                                            aabb.height)

    fun mkHRect(aabb: Rectangle, tol: Vector2) = Rectangle(aabb.x,
                                                            aabb.y + (aabb.height - aabb.height * tol.y) / 2,
                                                            aabb.width,
                                                            aabb.height * tol.y)
}
