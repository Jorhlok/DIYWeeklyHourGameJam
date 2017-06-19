package net.jorhlok.diyweeklygamejam

import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.DungeonMaster

/**
 * Created by joshm on 6/18/2017.
 */
class Fight(mapname: String,
            var MGR: MultiGfxRegister,
            var MAR: MultiAudioRegister) : DungeonMaster(mapname) {
}