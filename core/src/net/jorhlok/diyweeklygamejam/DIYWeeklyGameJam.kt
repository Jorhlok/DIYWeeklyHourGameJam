package net.jorhlok.diyweeklygamejam

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import net.jorhlok.multiav.MultiAudioRegister
import net.jorhlok.multiav.MultiGfxRegister
import net.jorhlok.oops.ObjectOrientedPlaySet
import com.badlogic.gdx.utils.Array
import net.jorhlok.oops.LabelledObject

class DIYWeeklyGameJam : ApplicationAdapter() {
    private var mgr: MultiGfxRegister? = null
    private var mar: MultiAudioRegister? = null
    private var oops: ObjectOrientedPlaySet? = null

    override fun create() {
        mgr = MultiGfxRegister()
        mar = MultiAudioRegister()
        mkav()
        mgr!!.Generate()
        mar!!.Generate()
        mar!!.setMusVolume(0.5f)
        mar!!.setSFXVolume(0.5f)

        oops = ObjectOrientedPlaySet()
        oops!!.FrameThreshold = 0.2f //low power game can probably handle 5 fps without physics breaking down

        oops!!.addTileMap("Outside", TmxMapLoader(InternalFileHandleResolver()).load("map/home.tmx"))
        oops!!.addTileMap("Inside", TmxMapLoader(InternalFileHandleResolver()).load("map/inside.tmx"))
        oops!!.addTileMap("Dark", TmxMapLoader(InternalFileHandleResolver()).load("map/Dark.tmx"))
        oops!!.addTileMap("Forest", TmxMapLoader(InternalFileHandleResolver()).load("map/Forest.tmx"))
        oops!!.addTileMap("Ruin", TmxMapLoader(InternalFileHandleResolver()).load("map/Ruin.tmx"))
        oops!!.addTileMap("Desert", TmxMapLoader(InternalFileHandleResolver()).load("map/Desert.tmx"))
        oops!!.addTileMap("text", TmxMapLoader(InternalFileHandleResolver()).load("map/text.tmx"))

        oops!!.GlobalData["Bat"] = LabelledObject("int",0)
        oops!!.GlobalData["Skelly"] = LabelledObject("int",0)
        oops!!.GlobalData["Slime"] = LabelledObject("int",0)
        oops!!.GlobalData["Ghost"] = LabelledObject("int",0)
        oops!!.GlobalData["Spider"] = LabelledObject("int",0)
        oops!!.GlobalData["Pos"] = LabelledObject("vec2", Vector2())
        oops!!.GlobalData["FromFight"] = LabelledObject("bool",false)
        oops!!.GlobalData["Monster"] = LabelledObject("Skelly")

        oops!!.addMasterScript("title",Title("",mgr!!,mar!!))
        oops!!.addMasterScript("Outside",DM("Outside",mgr!!,mar!!))
        oops!!.addMasterScript("Inside",DM("Inside",mgr!!,mar!!))
        oops!!.addMasterScript("Dark",DM("Dark",mgr!!,mar!!))
        oops!!.addMasterScript("Forest",DM("Forest",mgr!!,mar!!))
        oops!!.addMasterScript("Ruin",DM("Ruin",mgr!!,mar!!))
        oops!!.addMasterScript("Desert",DM("Desert",mgr!!,mar!!))
        oops!!.addMasterScript("Fight",Fight("text",mgr!!,mar!!))
        oops!!.launchScript("title")
    }

    override fun render() {
        val deltatime = Gdx.graphics.deltaTime
        oops!!.step(deltatime)
        try {
            oops!!.draw(deltatime)
        } catch (e: Exception) {
            System.err.println("Error drawing!")
            e.printStackTrace()
        }
    }

    override fun dispose() {
        oops?.dispose()
        mgr?.dispose()
        mar?.dispose()
    }

    fun mkav() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("gfx/libmono.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 14 * 1
        parameter.magFilter = Texture.TextureFilter.Nearest
        parameter.minFilter = Texture.TextureFilter.Linear
        parameter.hinting = FreeTypeFontGenerator.Hinting.Full
        mgr?.newFont("libmono",generator.generateFont(parameter),1f)
        generator.dispose()

        mar?.newMusic("1","bgm/Track One.mp3")
        mar?.newMusic("2","bgm/Track Two.mp3")
        mar?.newMusic("3","bgm/Track Three.mp3")
        mar?.newMusic("4","bgm/Return Home(Four).mp3")
        mar?.newMusic("5","bgm/Track Five.mp3")

        mar?.newSFX("beep","sfx/jorhlok/beep.wav")
        mar?.newSFX("bip","sfx/jorhlok/bip.wav")
        mar?.newSFX("boop","sfx/jorhlok/boop.wav")

        mar?.newSFX("back","sfx/NESFX/Back.wav")
        mar?.newSFX("denied1","sfx/NESFX/Denied1.wav")
        mar?.newSFX("denied2","sfx/NESFX/Denied2.wav")
        mar?.newSFX("select","sfx/NESFX/Select.wav")
        mar?.newSFX("start1","sfx/NESFX/Start1.wav")
        mar?.newSFX("start2","sfx/NESFX/Start2.wav")

//        Caused by: com.badlogic.gdx.utils.GdxRuntimeException: WAV files must be PCM: 3
//        mar?.newSFX("phone","sfx/wavestopmusic/8bit Cell phone.wav")
//        mar?.newSFX("arrow","sfx/wavestopmusic/Arrow Stick.wav")
//        mar?.newSFX("gun","sfx/wavestopmusic/Gun one.wav")
//        mar?.newSFX("laser1","sfx/wavestopmusic/Laser Slide Long.wav")
//        mar?.newSFX("laser2","sfx/wavestopmusic/Laser Slide.wav")
//        mar?.newSFX("crash","sfx/wavestopmusic/Mini Crash.wav")
//        mar?.newSFX("riser","sfx/wavestopmusic/Riser Chip.wav")
//        mar?.newSFX("robot","sfx/wavestopmusic/Robot Talk.wav")
//        mar?.newSFX("sling","sfx/wavestopmusic/Sling.wav")

        mgr?.newBuffer("main",640,360,640f,360f)
        mgr?.newBuffer("battle",640,360,640f,360f)

        mgr?.newImageRgb("things","gfx/Tiny16/Characters/!things.png",16,16)
        mgr?.newImageRgb("dead","gfx/Tiny16/Characters/\$dead.png",16,16)
        mgr?.newImageRgb("characters","gfx/Tiny16/Characters/characters.png",16,16)
        mgr?.newImageRgb("window","gfx/Tiny16/System/Window.png",8,8)
        mgr?.newImageRgb("a1","gfx/Tiny16/Tilesets/A1.png",16,16)
        mgr?.newImageRgb("b","gfx/Tiny16/Tilesets/B.png",16,16)

        mgr?.newSpriteRgb("winUpLf","window",4,0)
        mgr?.newSpriteRgb("winUp","window",5,0)
        mgr?.newSpriteRgb("winUpRt","window",7,0)
        mgr?.newSpriteRgb("winLf","window",4,1)
        mgr?.newSpriteRgb("winRt","window",7,1)
        mgr?.newSpriteRgb("winDnLf","window",4,4)
        mgr?.newSpriteRgb("winDn","window",5,4)
        mgr?.newSpriteRgb("winDnRt","window",7,4)
        mgr?.newSpriteRgb("spin0","window",6,4)
        mgr?.newSpriteRgb("spin1","window",7,4)
        mgr?.newSpriteRgb("spin2","window",6,5)
        mgr?.newSpriteRgb("spin3","window",6,4,1,1,false,false,3)
        mgr?.newSpriteRgb("spin4","window",7,4,1,1,false,false,3)
        mgr?.newSpriteRgb("spin5","window",6,5,1,1,false,false,3)
        mgr?.newSpriteRgb("spin6","window",6,4,1,1,false,false,2)
        mgr?.newSpriteRgb("spin7","window",7,4,1,1,false,false,2)
        mgr?.newSpriteRgb("spin8","window",6,5,1,1,false,false,2)
        mgr?.newSpriteRgb("spin9","window",6,4,1,1,false,false,1)
        mgr?.newSpriteRgb("spinA","window",7,4,1,1,false,false,1)
        mgr?.newSpriteRgb("spinB","window",6,5,1,1,false,false,1)

        mgr?.newAnimRgb("spin",Array<String>(arrayOf("spin0","spin1","spin2","spin3","spin4","spin5","spin6","spin7","spin8","spin9","spinA","spinB")),1/13f)

        mgr?.newSpriteRgb("bed","B",1,0,1,2)
        mgr?.newSpriteRgb("bedbottom","B",1,1)
        mgr?.newSpriteRgb("chair","B",2,0)
        mgr?.newSpriteRgb("statue","B",3,0)
        mgr?.newSpriteRgb("well","B",4,0)
        mgr?.newSpriteRgb("stairsUp","B",5,0)
        mgr?.newSpriteRgb("stairsDn","B",6,0)

        mgr?.newSpriteRgb("sleepingbag","B",0,1)
        mgr?.newSpriteRgb("table","B",2,1)
        mgr?.newSpriteRgb("dresser","B",3,1)
        mgr?.newSpriteRgb("column","B",4,1)

        mgr?.newSpriteRgb("water0","A1",0,1,2,2)
        mgr?.newSpriteRgb("water1","A1",2,1,2,2)
        mgr?.newSpriteRgb("water2","A1",4,1,2,2)

        mgr?.newSpriteRgb("door0","things",0,0)
        mgr?.newSpriteRgb("door1","things",0,1)
        mgr?.newSpriteRgb("door2","things",0,2)
        mgr?.newSpriteRgb("door3","things",0,3)
        mgr?.newSpriteRgb("gate0","things",3,0)
        mgr?.newSpriteRgb("gate1","things",3,1)
        mgr?.newSpriteRgb("gate2","things",3,2)
        mgr?.newSpriteRgb("gate3","things",3,3)
        mgr?.newSpriteRgb("chest0","things",6,0)
        mgr?.newSpriteRgb("chest1","things",6,1)
        mgr?.newSpriteRgb("chest2","things",6,2)
        mgr?.newSpriteRgb("chest3","things",6,3)
        mgr?.newSpriteRgb("pot0","things",9,0)
        mgr?.newSpriteRgb("pot1","things",9,1)
        mgr?.newSpriteRgb("pot2","things",9,2)
        mgr?.newSpriteRgb("pot3","things",9,3)

        mgr?.newSpriteRgb("firebig0","things",9,4)
        mgr?.newSpriteRgb("firebig1","things",10,4)
        mgr?.newSpriteRgb("firebig2","things",11,4)
        mgr?.newSpriteRgb("firemed0","things",9,5)
        mgr?.newSpriteRgb("firemed1","things",10,5)
        mgr?.newSpriteRgb("firemed2","things",11,5)
        mgr?.newSpriteRgb("firesml0","things",9,6)
        mgr?.newSpriteRgb("firesml1","things",10,6)
        mgr?.newSpriteRgb("firesml2","things",11,6)
        mgr?.newSpriteRgb("fireoff0","things",9,7)
        mgr?.newSpriteRgb("fireoff1","things",10,7)
        mgr?.newSpriteRgb("fireofff2","things",11,7)

        mgr?.newSpriteRgb("skellydead","dead",0,3)
        mgr?.newSpriteRgb("slimedead","dead",1,3)
        mgr?.newSpriteRgb("batdead","dead",2,3)
        mgr?.newSpriteRgb("ghostdead","dead",0,4)
        mgr?.newSpriteRgb("spiderdead","dead",1,4)

        mkchar("man",3,0)
        mkchar("woman",6,0)
        mkchar("Skelly",9,0)
        mkchar("Slime",0,4)
        mkchar("Bat",3,4)
        mkchar("Ghost",6,4)
        mkchar("Spider",9,4)
    }

    fun mkchar(str: String, x: Int, y: Int) {
        mgr?.newSpriteRgb("${str}Dn0","characters",x+0,y+0)
        mgr?.newSpriteRgb("${str}Dn1","characters",x+1,y+0)
        mgr?.newSpriteRgb("${str}Dn2","characters",x+2,y+0)
        mgr?.newSpriteRgb("${str}Lf0","characters",x+0,y+1)
        mgr?.newSpriteRgb("${str}Lf1","characters",x+1,y+1)
        mgr?.newSpriteRgb("${str}Lf2","characters",x+2,y+1)
        mgr?.newSpriteRgb("${str}Rt0","characters",x+0,y+2)
        mgr?.newSpriteRgb("${str}Rt1","characters",x+1,y+2)
        mgr?.newSpriteRgb("${str}Rt2","characters",x+2,y+2)
        mgr?.newSpriteRgb("${str}Up0","characters",x+0,y+3)
        mgr?.newSpriteRgb("${str}Up1","characters",x+1,y+3)
        mgr?.newSpriteRgb("${str}Up2","characters",x+2,y+3)

        mgr?.newAnimRgb("${str}StandDn",Array<String>(arrayOf("${str}Dn1")))
        mgr?.newAnimRgb("${str}WalkDn",Array<String>(arrayOf("${str}Dn0","${str}Dn1","${str}Dn2","${str}Dn1")),0.125f)
        mgr?.newAnimRgb("${str}StandLf",Array<String>(arrayOf("${str}Lf1")))
        mgr?.newAnimRgb("${str}WalkLf",Array<String>(arrayOf("${str}Lf0","${str}Lf1","${str}Lf2","${str}Lf1")),0.125f)
        mgr?.newAnimRgb("${str}StandRt",Array<String>(arrayOf("${str}Rt1")))
        mgr?.newAnimRgb("${str}WalkRt",Array<String>(arrayOf("${str}Rt0","${str}Rt1","${str}Rt2","${str}Rt1")),0.125f)
        mgr?.newAnimRgb("${str}StandUp",Array<String>(arrayOf("${str}Up1")))
        mgr?.newAnimRgb("${str}WalkUp",Array<String>(arrayOf("${str}Up0","${str}Up1","${str}Up2","${str}Up1")),0.125f)
    }
}
