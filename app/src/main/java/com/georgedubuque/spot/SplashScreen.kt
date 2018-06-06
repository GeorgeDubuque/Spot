package com.georgedubuque.spot

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.activity_spot_map.*

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in)
        val fadeInSlow = AnimationUtils.loadAnimation(this,R.anim.fade_in_slow)

        image_spot_logo.startAnimation(fadeIn)
        logo.startAnimation(fadeInSlow)

        fadeInSlow.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationRepeat(animation: Animation?) {
                // actually, I don't need this method but I have to implement this.
            }

            override fun onAnimationEnd(animation: Animation?) {

                startMainActivity()
            }

            override fun onAnimationStart(animation: Animation?) {
                // actually, I don't need this method but I have to implement this.
            }
        })

    }

    fun startMainActivity(){

        val intent = Intent(this,Map::class.java)
        startActivity(intent)
    }
}
