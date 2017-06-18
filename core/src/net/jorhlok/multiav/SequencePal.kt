package net.jorhlok.multiav

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

/**
 * Created by joshm on 5/29/2017.
 */
class SequencePal (
    var Name: String = "",
    var FrameNames: Array<String> = Array<String>(),
    var PalSeq: Array<Array<Short>?> = Array<Array<Short>?>(),
    var FrameTime: Float = 0f,
    var PlayMode: Animation.PlayMode = Animation.PlayMode.LOOP) {

    private class WhimsicalIdentifier(var sprite: FramePal, var palette: Array<Short>)

    private val NullFrame = FramePal()

    //generated
    private var Seq: Animation<WhimsicalIdentifier>? = null

    fun Generate(map: Map<String,FramePal>) {
        if (FrameNames.size > 0 && PalSeq.size > 0) {
            var lastpal = PalSeq[0]
            if (lastpal == null) {
                lastpal = Array<Short>()

                //there's probably a first frame and they probably all have the same number of colors, if not, oh well
                val s = map[FrameNames[0]]
                if (s != null) {
                    var maxi: Short = 0
                    for (k in s.Tile.keys) if (k > maxi) maxi = k
                    for (i in 0..maxi) lastpal.add(i.toShort())
                }
            }
            val sequence = Array<WhimsicalIdentifier>()
            for (i in 0..FrameNames.size-1) {
                var tmppal: Array<Short>? = null
                if (i < PalSeq.size) tmppal = PalSeq[i]
                if (tmppal == null) tmppal = lastpal
                else lastpal = tmppal

                var tmpframe = map[FrameNames[i]]
                if (tmpframe == null) tmpframe = NullFrame

                sequence.add(WhimsicalIdentifier(tmpframe,tmppal!!))
            }
            Seq = Animation<WhimsicalIdentifier>(FrameTime,sequence,PlayMode)
        }
    }

    fun draw(batch: Batch, palette: Array<Color>, indexoffset: Short, statetime: Float, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2? = null) {
        if (Seq != null) {
            val frame = Seq!!.getKeyFrame(statetime)
            frame.sprite.draw(batch, palette, frame.palette, indexoffset, x, y, sw, sh, rot, center)
        }
    }

    fun dispose() {
        Seq = null
    }
}