package com.game.jumper.game.scripts

import android.util.Log
import com.game.jumper.engine.GameObject
import com.game.jumper.engine.objects.Script
import com.game.jumper.graphics.JumperGLRenderer
import com.game.jumper.game.scripts.PlayerScript

class HatScript : Script() {

    companion object {
        var hat : Int = 0
    }

    override fun start() {
        //gyroscopeRotationTracker.start()
        Log.d("HatScript", "Started!")
    }

    override fun update() {
        super.update()

        gameObject.transform.position.y = PlayerScript.position.y + 1f

    }
}