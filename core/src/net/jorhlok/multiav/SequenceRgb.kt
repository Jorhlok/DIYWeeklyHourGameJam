package net.jorhlok.multiav

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

/**
 * Created by joshm on 5/30/2017.
 */
class SequenceRgb (
        var Name: String = "",
        var FrameNames: Array<String> = Array<String>(),
        var FrameTime: Float = 0f,
        var PlayMode: Animation.PlayMode = Animation.PlayMode.LOOP) {

    private val NullFrame = FrameRgb()

    //generated
    var Seq: Animation<FrameRgb>? = null

    fun Generate(map: Map<String,FrameRgb>) {
        if (FrameNames.size > 0) {
            val sequence = Array<FrameRgb>()
            for (i in 0..FrameNames.size-1) {
                var tmpframe = map[FrameNames[i]]
                if (tmpframe == null) tmpframe = NullFrame

                sequence.add(tmpframe)
            }
            Seq = Animation<FrameRgb>(FrameTime,sequence,PlayMode)
        }
    }

    fun draw(batch: Batch, statetime: Float, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2? = null, col: Color = Color(1f,1f,1f,1f)) {
        if (Seq != null) {
            val frame = Seq!!.getKeyFrame(statetime)
            frame.draw(batch, x, y, sw, sh, rot, center, col)
        }
    }

    fun dispose() {
        Seq = null
    }
}