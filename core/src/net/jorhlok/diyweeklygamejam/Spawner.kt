package net.jorhlok.diyweeklygamejam

import com.badlogic.gdx.math.Rectangle
import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister

/**
 * Created by joshm on 6/18/2017.
 */
class Spawner(var Mon: String,
              var rect: Rectangle, parent: DM, MGR: MultiGfxRegister, MAR: MultiAudioRegister) : MAVEntity(MGR, MAR) {
    var statetime = 0f
    val waittime = 2f
    init {
        Position.set(-20f,-20f)
        AABB.set(-2f,-2f,4f,4f)
        Parent = parent
        CollEntities = true
        val e = HashSet<String>()
        e.add("Plar")
        CollEntWhite = e
    }
    override fun prestep(deltatime: Float) {
        statetime += deltatime
        if (statetime > waittime) {
            statetime -= waittime
            Position.x = Math.random().toFloat() * rect.width + rect.x
            Position.y = Math.random().toFloat() * rect.height + rect.y
        }
    }

    override fun poststep(deltatime: Float) {
        for (m in Mailbox)
            if (m.label.startsWith("Coll")) {
//                System.out.println("Ambushed by $Mon")
                if (Parent != null && Parent is DM) {
                    Parent!!.Parent!!.GlobalData["Pos"]!!.obj = (Parent as DM).Player!!.Position
                    Parent!!.Parent!!.GlobalData["FromFight"]!!.obj = true
                    val num = Parent!!.Parent!!.GlobalData[Mon]!!.obj as Int
                    Parent!!.Parent!!.GlobalData[Mon]!!.obj = num + 1
                    Parent!!.Parent!!.GlobalData["Monster"]!!.label = Mon
                    Parent!!.Parent!!.launchScript("Fight")
                }
            }
    }

    override fun draw(deltatime: Float) {
        MGR.drawRgb("${Mon}WalkDn",statetime,Position.x,Position.y)
    }
}