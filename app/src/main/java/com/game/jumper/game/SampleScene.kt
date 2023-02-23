package com.game.jumper.game

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.game.jumper.R
import com.game.jumper.databinding.ActivityGameBinding
import com.game.jumper.engine.GameObject
import com.game.jumper.engine.Scene
import com.game.jumper.game.scripts.PlatformScript
import com.game.jumper.game.scripts.PlayerScript
import com.game.jumper.graphics.JumperQuad
import com.game.jumper.level.LevelGenerator
import com.game.jumper.level.Platform
import com.game.jumper.math.Vector2
import com.game.jumper.motionSensor.MotionSensorListener
import com.game.jumper.engine.GameLoopGl
import com.game.jumper.level.Player
import com.game.jumper.graphics.JumperGLRenderer
import com.game.jumper.layout.GameActivity
import com.game.jumper.model.HighScoreViewModel

class SampleScene(context: Context) : Scene(context) {
    //private var object1 : GameObject
    private val gameBinding: GameActivity? = context as? GameActivity
    private var levelObject : GameObject

    private var playerObj : GameObject
    private var numPlatform : Int = 30

    private var platform: MutableList<Platform>

    private var width : Int = 0
    private var height : Int = 0
    private var quad2 : JumperQuad

    private val gyroscopeRotationTracker = MotionSensorListener(context)
    private var currentRotation = gyroscopeRotationTracker.getCurrentRoll()

    private var score : Int = 0
    private var isJumping : Boolean = false
    private var time : Float = 0f

    private var isDie : Boolean = false

    private lateinit var deathDialog : Dialog

    init {
        gyroscopeRotationTracker.start()
        width = Resources.getSystem().displayMetrics.widthPixels
        height = Resources.getSystem().displayMetrics.heightPixels

        val quad1 = JumperQuad(context, "art/BPlayer_Idle.png")
        quad2 = JumperQuad(context, "art/platform.png")

        gameObjects.clear()
        paused = false

        /*object1 = createNewObject()
        object1.name = "Spinning Object"
        object1.transform.position.x = 0f
        object1.transform.position.y = 0f
        object1.transform.scale.x = 1.0f
        object1.transform.scale.y = 1.0f
        object1.addScript<SpinningScript>()
        val quad1 = JumperQuad(context, "art/BPlayer_Idle.png")
        object1.quad = quad1*/

        // create level using LevelGenerator
        platform = LevelGenerator().generateLevel(width, height, numPlatform)

        for (i in platform.indices) {
            levelObject = createNewObject()
            levelObject.name = "Platform"
            levelObject.transform.position.x = (platform[i].x.toFloat() / width  - .5f) * 10
            levelObject.transform.position.y = (platform[i].y.toFloat() / height - .5f) * 20
            levelObject.transform.scale.x = platform[i].sizeX
            levelObject.transform.scale.y = platform[i].sizeY
            levelObject.addScript<PlatformScript>()
            levelObject.quad = quad2
        }
        // First platform
        levelObject = createNewObject()
        levelObject.name = "Platform"
        levelObject.transform.position.x = 0f
        levelObject.transform.position.y = -5f
        levelObject.transform.scale.x = 3f
        levelObject.transform.scale.y = 0.2f
        levelObject.addScript<PlatformScript>()
        levelObject.quad = quad2
        platform.add(Platform(levelObject.transform.position.x, levelObject.transform.position.y))

        playerObj = createNewObject()
        playerObj.name = "Player"
        playerObj.transform.position.x = 0f
        playerObj.transform.position.y = -1f
        playerObj.transform.scale.x = 1.0f
        playerObj.transform.scale.y = 1.0f
        playerObj.addScript<PlayerScript>()

        playerObj.quad = quad1
    }

    override fun start() {
        super.start()
        deathDialog = Dialog(context)
    }

    override fun update() {
        super.update()
        if(paused) return
        currentRotation = gyroscopeRotationTracker.getCurrentRoll()

        val player = findObject("Player")

        val playerPos = Vector2(player.transform.position.x, player.transform.position.y)

        for (i in gameObjects.indices) {

            if (gameObjects[i].name == "Platform") {

                val platformPos = Vector2(gameObjects[i].transform.position.x, gameObjects[i].transform.position.y)

                if (checkCollided(playerPos, PlayerScript.velocity, platformPos) && PlayerScript.velocity.y < -.5f) {

                    if (!platform[i].isJumped) {
                        score += 100
                        GameActivity.UpdateScore(score)
                        platform[i].isJumped = true
                    }


                    isJumping = true
                    PlayerScript.velocity.y = 5f
                }

                var lowestPlatform = -12f

                if (gameObjects[i].transform.position.y < JumperGLRenderer.camPos.y-12) {
                    var highestPlatform = 0f
                    lowestPlatform = 10000000f
                    // loop to get highest platform
                    for (platformLoop in gameObjects) {
                        if (platformLoop.name == "Platform") {
                            if (highestPlatform < platformLoop.transform.position.y)
                                highestPlatform = platformLoop.transform.position.y

                            if (lowestPlatform > platformLoop.transform.position.y)
                                lowestPlatform = platformLoop.transform.position.y
                        }
                    }
                    gameObjects[i].transform.position.y = highestPlatform + (1..10 ).random() % 4
                    platform[i].isJumped = false
                }

                if (player.transform.position.y < lowestPlatform - 10f ) isDie = true
            }
        }

        if (isJumping) {
            time += GameLoopGl.deltaTime.toFloat()
            if (time > 1f) {
                PlayerScript.velocity.y = 0f
                isJumping = false
                time = 0f
            }
        }

        if (isDie) {
            //TODO: if DIE go game over
            gameBinding?.insertHighScoreToDatabase("XUANISCUTEEEEEEE", GameActivity.score)
            
            Log.d("Score", "Die")
            deathDialog.setContentView(R.layout.popup_death)
            deathDialog.setCancelable(false)
            deathDialog.show()
            paused = true
        }
    }
}

private fun checkCollided( playerPos : Vector2, playerVel:Vector2, platformPos : Vector2) : Boolean {
    val playerVector = Vector2(playerPos.x - platformPos.x, playerPos.y - platformPos.y - .5f)
    val upVector = Vector2(0f,1f)

    val dot = upVector.dotProduct((playerVector))
    // check if it is above the platform first
    if ((playerPos.x < (platformPos.x + 1.5f) && playerPos.x > (platformPos.x - 1.5f)) && dot >= -1f) {
        // Check if the player on next frame will be below the platform
        if ((playerPos.y + playerVel.y < platformPos.y))
        {
            // check if it hits 0
            if(dot <= 0f && dot >= -6f) {
                //Log.d("CheckCollision", "Yes, $dot")
                return true
            }
        }
    }
    return false
}

