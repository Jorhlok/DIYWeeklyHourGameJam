package net.jorhlok.diyweeklygamejam

import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.Entity

/**
 * Created by joshm on 6/18/2017.
 */
abstract class MAVEntity(
        var MGR: MultiGfxRegister,
        var MAR: MultiAudioRegister) : Entity()  {
    companion object {
        open fun getId() = 0
    }
}