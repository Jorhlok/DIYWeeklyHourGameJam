package net.jorhlok.diyweeklygamejam

import com.badlogic.gdx.math.Rectangle
import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.LabelledObject

/**
 * Created by joshm on 6/18/2017.
 */
class Transition(name: String, r: Rectangle, parent: DM, MGR: MultiGfxRegister, MAR: MultiAudioRegister) : MAVEntity(MGR, MAR) {
    init {
        Parent = parent
        Name = name
        Type = "Transition"
        Position.set(r.x/16,r.y/16)
        AABB.set(0f,0f,r.width/16,r.height/16)
//        Physics = true
        CollEntities = true
        val e = HashSet<String>()
        e.add("Plar")
        CollEntWhite = e
    }

    override fun poststep(deltatime: Float) {
        for (m in Mailbox)
            if (m.label.startsWith("Coll")) {
                Parent!!.Parent!!.GlobalData["FromFight"]!!.obj = false
                Parent?.Parent?.launchScript(Name)
            }
    }
}