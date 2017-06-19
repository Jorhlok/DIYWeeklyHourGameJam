package net.jorhlok.diyweeklygamejam

import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister

/**
 * Created by joshm on 6/19/2017.
 */
class Blocker(x: Float, y: Float, MGR: MultiGfxRegister, MAR: MultiAudioRegister) : MAVEntity(MGR, MAR) {
    init {
        Position.set(x,y)
        Type = "Blocker"
        CollEntities = true //allow collisions
        CollEntWhite = emptySet() //but I don't pay attention
        AABB.set(0f,0f,1f,1f)
    }
}