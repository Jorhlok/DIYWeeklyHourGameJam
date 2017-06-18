package net.jorhlok.diyweeklygamejam

import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister

/**
 * Created by joshm on 6/18/2017.
 */
class Plar(MGR: MultiGfxRegister, MAR: MultiAudioRegister) : MAVEntity(MGR, MAR) {
    var up = false
    var dn = false
    var lf = false
    var rt = false
    var statetime = 0f
    var drawdir = 0 //up,lf,dn,rt
    var charname = "woman"

    init{
        AABB.set(0f, 0f, 1f, 0.5f)
        Position.set(20f,1f)
        Physics = true
        CollTiles = true
    }

    override fun prestep(deltatime: Float) {
        for (p in Mailbox) {
            if (p.label.startsWith("Ctrl")) {
                when (p.label) {
                    "CtrlUp" -> up = p.obj as Boolean
                    "CtrlDn" -> dn = p.obj as Boolean
                    "CtrlLf" -> lf = p.obj as Boolean
                    "CtrlRt" -> rt = p.obj as Boolean
                }
            }
        }

        if (up && !dn) {
            drawdir = 0
            Velocity.y = 4f
        } else if (dn && !up) {
            drawdir = 2
            Velocity.y = -4f
        } else {
            Velocity.y = 0f
        }

        if (lf && !rt) {
            drawdir = 1
            Velocity.x = -4f
        } else if (rt && !lf) {
            drawdir = 3
            Velocity.x = 4f
        } else {
            Velocity.x = 0f
        }
    }

    override fun draw(deltatime: Float) {
        statetime += deltatime
        if (Velocity.isZero) when (drawdir) {
            0 -> MGR.drawRgb("${charname}StandUp",statetime,Position.x+0.5f,Position.y+0.5f)
            1 -> MGR.drawRgb("${charname}StandLf",statetime,Position.x+0.5f,Position.y+0.5f)
            2 -> MGR.drawRgb("${charname}StandDn",statetime,Position.x+0.5f,Position.y+0.5f)
            else -> MGR.drawRgb("${charname}StandRt",statetime,Position.x+0.5f,Position.y+0.5f)
        } else when (drawdir) {
            0 -> MGR.drawRgb("${charname}WalkUp",statetime,Position.x+0.5f,Position.y+0.5f)
            1 -> MGR.drawRgb("${charname}WalkLf",statetime,Position.x+0.5f,Position.y+0.5f)
            2 -> MGR.drawRgb("${charname}WalkDn",statetime,Position.x+0.5f,Position.y+0.5f)
            else -> MGR.drawRgb("${charname}WalkRt",statetime,Position.x+0.5f,Position.y+0.5f)
        }
    }
}