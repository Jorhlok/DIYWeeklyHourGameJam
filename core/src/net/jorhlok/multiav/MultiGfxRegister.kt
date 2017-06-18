package net.jorhlok.multiav

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import java.util.HashMap

/**
 * Multi-Function Audio-Visual Organization Engine
 * @author Jorhlok
 */
class MultiGfxRegister {
    enum class DrawingState {sprite,shape,off}

    //indexed gfx
    private var imagePal = HashMap<String, ImagePal>()
    private var framePal = HashMap<String, FramePal>()
    private var sequencePal = HashMap<String, SequencePal>()

    //rgb gfx
    private var imageRgb = HashMap<String, ImageRgb>()
    private var frameRgb = HashMap<String, FrameRgb>()
    private var sequenceRgb = HashMap<String, SequenceRgb>()

    //bitmap fonts
    private var fonts = HashMap<String, BitmapFont>()
    private var fontsampling = HashMap<String, Float>()

    //framebuffers
    private var buffers = HashMap<String, PixBuf>()
    private var mybuf = ""

    //tile layer translation
    private var tiletrans = HashMap<String, HashMap<Int,TileTranslator>>()


    private var batch: Batch? = null
    private var shape: ShapeRenderer? = null
    private var shapetype: ShapeRenderer.ShapeType = ShapeRenderer.ShapeType.Line
    private var status = DrawingState.off
    private var scalarCurrent = 1f

    var camera = OrthographicCamera()
    var palette = Array<Color>()
    var scalar = 1f

    fun newImagePal(key: String, uri: String, tw: Int, th: Int) {
        imagePal[key]?.dispose()
        imagePal.put(key, ImagePal(key, uri, tw, th))
    }

    fun newSpritePal(key: String, img: String, tx: Int, ty: Int, tw: Int = 1, th: Int = 1, hf: Boolean = false, vf: Boolean = false, rot90: Int = 0) {
        framePal.put(key, FramePal(key, img, tx, ty, tw, th, hf, vf, rot90))
    }

    fun newAnimPal(key: String, frames: Array<String>, palseq: Array<Array<Short>?> = Array<Array<Short>?>(), speed: Float = 0f, mode: Animation.PlayMode = Animation.PlayMode.LOOP) {
        sequencePal.put(key, SequencePal(key, frames, palseq, speed, mode))
    }

    fun newImageRgb(key: String, uri: String, tw: Int, th: Int) {
        imageRgb[key]?.dispose()
        imageRgb.put(key, ImageRgb(key, uri, tw, th))
    }

    fun newSpriteRgb(key: String, img: String, tx: Int, ty: Int, tw: Int = 1, th: Int = 1, hf: Boolean = false, vf: Boolean = false, rot90: Int = 0) {
        frameRgb.put(key, FrameRgb(key, img, tx, ty, tw, th, hf, vf, rot90))
    }

    fun newAnimRgb(key: String, frames: Array<String>, speed: Float = 0f, mode: Animation.PlayMode = Animation.PlayMode.LOOP) {
        sequenceRgb.put(key, SequenceRgb(key, frames, speed, mode))
    }

    fun newFont(key: String, f: BitmapFont, sampling: Float = 1f) {
        fonts.put(key,f)
        fontsampling.put(key,sampling)
    }

    fun newBuffer(key: String, width: Int, height: Int, camwidth: Float, camheight: Float, scalar: Float = 1f, format: Pixmap.Format = Pixmap.Format.RGBA8888) {
        buffers[key] = PixBuf(width,height,camwidth,camheight,scalar,format)
    }

    fun newMapTilePal(mapkey: String, id: Int, paloff: Short, anim: String, animH: String = "", animV:String = ", ", animHV:String = "", rotate: Boolean = false) {
        var map = tiletrans[mapkey]
        if (map == null) {
            tiletrans[mapkey] = HashMap<Int,TileTranslator>()
            map = tiletrans[mapkey]
        }
        map!![id] = TileTranslator(true,paloff.toInt(),rotate,anim,animH,animV,animHV)
    }

    fun newMapTileRgb(mapkey: String, id: Int, anim: String, animH: String = "", animV:String = ", ", animHV:String = "", rotate: Boolean = false, col: Color = Color(1f,1f,1f,1f)) {
        var map = tiletrans[mapkey]
        if (map == null) {
            tiletrans[mapkey] = HashMap<Int,TileTranslator>()
            map = tiletrans[mapkey]
        }
        map!![id] = TileTranslator(false,col.toIntBits(),rotate,anim,animH,animV,animHV)
    }


    fun Generate() {
        batch = SpriteBatch()
        shape = ShapeRenderer()
        shape!!.setAutoShapeType(true)
        camera.setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch!!.projectionMatrix = camera.combined
        shape!!.projectionMatrix = camera.combined
        //indexed color
        for (t in imagePal.values)
            t.Generate()
        for (s in framePal) {
            s.value.Generate(imagePal)
            val defaultpal = Array<Array<Short>?>(arrayOf(Array<Short>()))
            var maxi: Short = 0
            for (k in s.value.Tile.keys) if (k > maxi) maxi = k
            for (i in 0..maxi) defaultpal[0]!!.add(i.toShort())

            newAnimPal("_" + s.key, Array<String>(arrayOf(s.key)), defaultpal) //auto generate single frame animation for each framePal
        }
        for (a in sequencePal.values)
            a.Generate(framePal)
        //rgba color
        for (t in imageRgb.values)
            t.Generate()
        for (s in frameRgb) {
            s.value.Generate(imageRgb)
            newAnimRgb("_" + s.key, Array<String>(arrayOf(s.key))) //auto generate single frame animation for each frameRgb
        }
        for (a in sequenceRgb.values)
            a.Generate(frameRgb)
    }

    fun dispose() {
        drawingOff()
        batch?.dispose()
        batch = null

        //indexed gfx
        for (t in imagePal.values)
            t.dispose()
        for (s in framePal.values)
            s.dispose()
        for (a in sequencePal.values)
            a.dispose()
        imagePal = HashMap<String, ImagePal>()
        framePal = HashMap<String, FramePal>()
        sequencePal = HashMap<String, SequencePal>()

        //rgb gfx
        for (t in imageRgb.values)
            t.dispose()
        for (s in frameRgb.values)
            s.dispose()
        for (a in sequenceRgb.values)
            a.dispose()
        imageRgb = HashMap<String, ImageRgb>()
        frameRgb = HashMap<String, FrameRgb>()
        sequenceRgb = HashMap<String, SequenceRgb>()

        //bitmap fonts
        for (f in fonts.values)
            f.dispose()
        fonts = HashMap<String, BitmapFont>()

        //framebuffers
        for (b in buffers.values)
            b.dispose()
        buffers = HashMap<String, PixBuf>()
    }

    private fun drawingSprite() {
        when (status) {
            DrawingState.off -> {
                batch?.begin()
                status = DrawingState.sprite
            }
            DrawingState.shape -> {
                shape?.flush()
                shape?.end()
                batch?.begin()
                status = DrawingState.sprite
            }
        }
    }

    private fun drawingShape() {
        when (status) {
            DrawingState.off -> {
                shape?.begin(shapetype)
                status = DrawingState.shape
            }
            DrawingState.sprite -> {
                batch?.flush()
                batch?.end()
                shape?.begin(shapetype)
                status = DrawingState.shape
            }
        }
    }

    private fun drawingOff() {
        when (status) {
            DrawingState.sprite -> {
                batch?.flush()
                batch?.end()
                status = DrawingState.off
            }
            DrawingState.shape -> {
                shape?.flush()
                shape?.end()
                status = DrawingState.off
            }
        }
    }


    //regular drawing

    fun drawPal(anim: String, indexoffset: Short, statetime: Float, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2? = null) {
        drawingSprite()
        if (batch != null) sequencePal[anim]?.draw(batch!!,palette,indexoffset,statetime,x,y,sw*scalarCurrent,sh*scalarCurrent,rot,center?.scl(1/scalarCurrent))
    }

    fun drawRgb(anim: String, statetime: Float, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2? = null, col: Color = Color(1f,1f,1f,1f)) {
        drawingSprite()
        if (batch != null) sequenceRgb[anim]?.draw(batch!!,statetime,x,y,sw*scalarCurrent,sh*scalarCurrent,rot,center?.scl(1/scalarCurrent), col)
    }

    fun drawString(f: String, str: String, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2 = Vector2(0.5f,0.5f), col: Color = Color(1f,1f,1f,1f)) {
        drawGlyphLayout(f,stringLayout(f,str),x,y,sw,sh,rot,center,col)
    }

    fun drawGlyphLayout(font: String, lay: GlyphLayout, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2 = Vector2(0.5f,0.5f), col: Color = Color(1f,1f,1f,1f)) {
        drawingSprite()
        val f = fonts[font]
        var sampling = fontsampling[font]
        if (f != null && sampling!= null && batch != null) {
            sampling *= scalarCurrent
            val begin = Vector2(x-lay.width*center.x,y+f.lineHeight+lay.height*center.y)
            val pages = f.regions
            val oldcol = batch!!.color
            batch!!.color = col
            for (run in lay.runs) {
                var xadv = 0f
                for (i in 0..run.glyphs.size-1) {
                    val g = run.glyphs[i]
                    xadv += run.xAdvances[i]
                    val pt = transformPoint(Vector2(run.x+xadv+g.xoffset,run.y+g.yoffset).add(begin),sw*sampling,sh*sampling,rot,Vector2(x,y))
                    batch!!.draw(TextureRegion(pages[g.page],g.srcX,g.srcY,g.width,g.height),
                            pt.x,pt.y,0f,0f,g.width.toFloat(),g.height.toFloat(),sw*sampling,sh*sampling,rot)

                }
            }
            batch!!.color = oldcol
        }
    }

    fun transformPoint(pt: Vector2, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2 = Vector2()): Vector2 {
        return pt.cpy().sub(center).scl(sw,sh).rotate(rot).add(center)
    }

    fun stringLayout(font: String, str: String): GlyphLayout {
        var f = fonts[font]
        if (f != null) return GlyphLayout(f,str)
        return GlyphLayout()
    }

    fun stringLayout(font: String, str: String, col: Color, targetWidth: Float, halign: Int, wrap: Boolean): GlyphLayout {
        var f = fonts[font]
        if (f != null) return GlyphLayout(f,str,col,targetWidth,halign,wrap)
        return GlyphLayout()
    }


    //draw vector graphics

    fun fillShapes() {
        shapetype = ShapeRenderer.ShapeType.Filled
        drawingShape()
        shape?.set(shapetype)
    }

    fun lineShapes() {
        shapetype = ShapeRenderer.ShapeType.Line
        drawingShape()
        shape?.set(shapetype)
    }

    fun drawPoint(x: Float, y: Float, z: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.point(x,y,z)
    }

    fun drawLine(x: Float, y: Float, z: Float, x2: Float, y2: Float, z2: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.line(x,y,z,x2,y2,z2)
    }

    fun drawLine(v0: Vector3, v1: Vector3, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.line(v0,v1)
    }

    fun drawLine(x: Float, y: Float, x2: Float, y2: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.line(x,y,x2,y2)
    }

    fun drawLine(v0: Vector2, v1: Vector2, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.line(v0,v1)
    }

    fun drawLine(x: Float, y: Float, x2: Float, y2: Float, c1: Color, c2: Color) {
        shape?.line(x,y,x2,y2,c1,c2)
    }

    fun drawLine(x: Float, y: Float, z: Float, x2: Float, y2: Float, z2: Float, c1: Color, c2: Color) {
        shape?.line(x,y,z,x2,y2,z2,c1,c2)
    }

    fun drawCurve(x1: Float, y1: Float, cx1: Float, cy1: Float, cx2: Float, cy2: Float, x2: Float, y2: Float, segments: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.curve(x1,y1,cx1,cy1,cx2,cy2,x2,y2,segments)
    }

    fun drawTriangle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.triangle(x1,y1,x2,y2,x3,y3)
    }

    fun drawTriangle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, col1: Color, col2: Color, col3: Color) {
        shape?.triangle(x1,y1,x2,y2,x3,y3,col1,col2,col3)
    }

    fun drawRect(x: Float, y: Float, width: Float, height: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.rect(x,y,width,height)
    }

    fun drawRect(x: Float, y: Float, width: Float, height: Float, col1: Color, col2: Color, col3: Color, col4: Color) {
        shape?.rect(x,y,width,height,col1,col2,col3,col4)
    }

    fun drawRect(x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float,
             degrees: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.rect(x,y,originX,originY,width,height,scaleX,scaleY,degrees)
    }

    fun drawRect(x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float,
             degrees: Float, col1: Color, col2: Color, col3: Color, col4: Color) {
        shape?.rect(x,y,originX,originY,width,height,scaleX,scaleY,degrees,col1,col2,col3,col4)

    }

    fun drawRectLine(x1: Float, y1: Float, x2: Float, y2: Float, width: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.rectLine(x1,y1,x2,y2,width)
    }

    fun drawRectLine(x1: Float, y1: Float, x2: Float, y2: Float, width: Float, c1: Color, c2: Color) {
        shape?.rectLine(x1,y1,x2,y2,width,c1,c2)
    }

    fun drawRectLine(p1: Vector2, p2: Vector2, width: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.rectLine(p1,p2,width)
    }

    fun drawBox(x: Float, y: Float, z: Float, width: Float, height: Float, depth: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.box(x,y,z,width,height,depth)
    }

    fun drawX(x: Float, y: Float, size: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.x(x,y,size)
    }

    fun drawX(p: Vector2, size: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.x(p,size)
    }

    fun drawArc(x: Float, y: Float, radius: Float, start: Float, degrees: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.arc(x,y,radius,start,degrees)
    }

    fun drawArc(x: Float, y: Float, radius: Float, start: Float, degrees: Float, segments: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.arc(x,y,radius,start,degrees,segments)
    }

    fun drawCircle(x: Float, y: Float, radius: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.circle(x,y,radius)
    }

    fun drawCircle(x: Float, y: Float, radius: Float, segments: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.circle(x,y,radius,segments)
    }

    fun drawEllipse(x: Float, y: Float, width: Float, height: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.ellipse(x,y,width,height)
    }

    fun drawEllipse(x: Float, y: Float, width: Float, height: Float, segments: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.ellipse(x,y,width,height,segments)
    }

    fun drawEllipse(x: Float, y: Float, width: Float, height: Float, rotation: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.ellipse(x,y,width,height,rotation)
    }

    fun drawEllipse(x: Float, y: Float, width: Float, height: Float, rotation: Float, segments: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.ellipse(x,y,width,height,rotation,segments)
    }

    fun drawCone(x: Float, y: Float, z: Float, radius: Float, height: Float, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.cone(x,y,z,radius,height)
    }

    fun drawCone(x: Float, y: Float, z: Float, radius: Float, height: Float, segments: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.cone(x,y,z,radius,height,segments)
    }

    fun drawPolygon(vertices: FloatArray, offset: Int, count: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.polygon(vertices,offset,count)
    }

    fun drawPolygon(vertices: FloatArray, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.polygon(vertices)
    }

    fun drawPolyline(vertices: FloatArray, offset: Int, count: Int, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.polyline(vertices,offset,count)
    }

    fun drawPolyline(vertices: FloatArray, col: Color? = null) {
        drawingShape()
        if (col != null) shape?.color = col
        shape?.polyline(vertices)
    }


    //drawing framebuffers

    fun getBufCam(key: String): OrthographicCamera? {
        val buf = buffers[key]
        if (buf != null) return buf.cam
        return null
    }

    fun setBufScalar(key: String, s: Float = 1f) {
        val buf = buffers[key]
        if (buf != null) {
            buf.scalar = s
            if (key == mybuf) scalarCurrent = s
        }
    }

    fun getBufScalar(key:String): Float {
        val buf = buffers[key]
        if (buf != null) return buf.scalar
        return 0f
    }

    fun clear(r: Float = 0f, g: Float = 0f, b: Float = 0f, a: Float = 1f) {
        Gdx.gl.glClearColor(r,g,b,a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    fun clear(col: Color) {
        clear(col.r,col.g,col.b,col.a)
    }

    fun blank() {
        clear(0f,0f,0f,0f)
    }

    fun flush() {
        drawingOff()
    }

    fun startBuffer(key: String) {
        stopBuffer()
        val buf = buffers[key]
        if (buf != null) {
            if (status != DrawingState.off) drawingOff()
            mybuf = key
            buf.begin()
            buf.cam.update()
            batch!!.projectionMatrix = buf.cam.combined
            shape!!.projectionMatrix = buf.cam.combined
            scalarCurrent = buf.scalar
        }
    }

    fun stopBuffer() {
        if (mybuf != "") {
            drawingOff()
            buffers[mybuf]?.end()
            camera.update()
            batch!!.projectionMatrix = camera.combined
            shape!!.projectionMatrix = camera.combined
            scalarCurrent = scalar
        }
    }

    fun updateCam() {
        camera.update()
        if (mybuf == "") {
            batch!!.projectionMatrix = camera.combined
            shape!!.projectionMatrix = camera.combined
            scalarCurrent = scalar
        }
    }

    fun drawBuffer(key:String, x: Float = 0f, y: Float = 0f, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2 = Vector2(), col: Color = Color(1f,1f,1f,1f)) {
        var buf = buffers[key]
        if (buf != null && batch != null) {
            drawingSprite()
            val oldcol = batch!!.color
            batch!!.color = col
            batch!!.draw(TextureRegion(buf.tex),x,y,center.x,center.y,buf.tex.regionWidth.toFloat(),buf.tex.regionHeight.toFloat(),sw*scalarCurrent,sh*scalarCurrent,rot)
            batch!!.color = oldcol
        }
    }

    fun drawBuffer(key:String, srcx: Int, srcy: Int, srcw: Int, srch: Int, x: Float, y: Float, sw: Float = 1f, sh: Float = 1f, rot: Float = 0f, center: Vector2 = Vector2(), col: Color = Color(1f,1f,1f,1f)) {
        var buf = buffers[key]
        if (buf != null && batch != null) {
            drawingSprite()
            val oldcol = batch!!.color
            batch!!.color = col
            batch!!.draw(TextureRegion(buf.tex,srcx,srcy,srcw,srch),x,y,center.x,center.y,buf.tex.regionWidth.toFloat(),buf.tex.regionHeight.toFloat(),sw*scalarCurrent,sh*scalarCurrent,rot)
            batch!!.color = oldcol
        }
    }


    fun drawTileLayer(layer: TiledMapTileLayer, key: String, unitScale: Float, statetime: Float, camin: OrthographicCamera? = null) {
        //this method is adapted from OrthogonalTiledMapRenderer.renderTileLayer(TiledMapTileLayer)
        val layerWidth = layer.width
        val layerHeight = layer.height

        val layerTileWidth = layer.tileWidth * unitScale * scalarCurrent
        val layerTileHeight = layer.tileHeight * unitScale * scalarCurrent


        var cam = camera
        if (camin != null) cam = camin
        else if (mybuf != "") cam = getBufCam(mybuf)!!

        //below 5 lines adapted from BatchTiledMapRenderer.setView(OrthographicCamera)
        val width = cam.viewportWidth * cam.zoom
        val height = cam.viewportHeight * cam.zoom
        val w = width * Math.abs(cam.up.y) + height * Math.abs(cam.up.x)
        val h = height * Math.abs(cam.up.y) + width * Math.abs(cam.up.x)
        var viewBounds = Rectangle(cam.position.x - w / 2, cam.position.y - h / 2, w, h)


        val col1 = Math.max(0, (viewBounds.x / layerTileWidth).toInt())
        val col2 = Math.min(layerWidth, ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth).toInt())

        val row1 = Math.max(0, (viewBounds.y / layerTileHeight).toInt())
        val row2 = Math.min(layerHeight, ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight).toInt())

        var y = row2 * layerTileHeight
        val xStart = col1 * layerTileWidth

        val maptrans = tiletrans[key]

        if (maptrans != null) for (row in row2 downTo row1) {
            var x = xStart
            for (col in col1..col2 - 1) {
                val cell = layer.getCell(col, row)
                if (cell == null) {
                    x += layerTileWidth
                    continue
                }

                maptrans[cell.tile?.id]?.draw(this,cell,statetime,x,y,unitScale,unitScale)

                x += layerTileWidth
            }
            y -= layerTileHeight
        }
    }
}




