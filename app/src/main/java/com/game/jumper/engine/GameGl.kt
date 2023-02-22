package com.game.jumper.engine

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.game.jumper.graphics.JumperGLRenderer
import com.game.jumper.motionSensor.MotionSensorListener
import android.app.Activity

/**
 * Manages all objects in the game and is responsible for updating all states and render
 * all objects to the screen
 */
class GameGl : GLSurfaceView {
    lateinit var gameLoop: GameLoopGl
    var renderer : JumperGLRenderer

    constructor(context: Context) : super(context) {
        renderer = JumperGLRenderer(context)
        isFocusable = true
        initOpenGLView()

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        renderer = JumperGLRenderer(context)
        isFocusable = true
        initOpenGLView()
    }

//    override fun surfaceCreated(holder: SurfaceHolder) {
//        //initOpenGLView()
//    }
//
//    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
//    override fun surfaceDestroyed(holder: SurfaceHolder) {}
//    override fun draw(canvas: Canvas) {
//        super.draw(canvas)
//        drawUPS(canvas)
//        drawFPS(canvas)
//    }
//
//    fun drawUPS(canvas: Canvas) {
//        val averageUPS = java.lang.Double.toString(gameLoop.averageUPS)
//        val paint = Paint()
//        val color = ContextCompat.getColor(context, R.color.magenta)
//        paint.color = color
//        paint.textSize = 50F
//        canvas.drawText("UPS: $averageUPS", 100f, 50f, paint)
//    }
//
//    fun drawFPS(canvas: Canvas) {
//        val averageFPS = java.lang.Double.toString(gameLoop.averageFPS)
//        val paint = Paint()
//        val color = ContextCompat.getColor(context, R.color.magenta)
//        paint.color = color
//        paint.textSize = 50F
//        canvas.drawText("FPS: $averageFPS", 100f, 100f, paint)
//    }
//
//    fun update() {
//
//    }

    fun initOpenGLView() {
        val surfaceHolder = holder
        surfaceHolder.addCallback(this)
        setEGLContextClientVersion(2)
        setPreserveEGLContextOnPause(true)
        setRenderer(renderer)
        setRenderMode(RENDERMODE_WHEN_DIRTY)
        gameLoop = GameLoopGl(context, renderer, surfaceHolder)
        renderer.loadView(this)
        renderer.loadGameLoop(gameLoop)

    }
}