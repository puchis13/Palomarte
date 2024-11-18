package com.example.palomarapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
// import android.net.Uri // Import relacionado con el video, ahora comentado
// import android.widget.VideoView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // El VideoView está deshabilitado
        /*
        val videoView: VideoView = findViewById(R.id.videoView)

        // Establece la URI del video (puede ser un archivo local o una URL)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.intro_video)
        videoView.setVideoURI(videoUri)

        // Inicia el video automáticamente
        videoView.setOnPreparedListener { it.isLooping = true } // Loopa el video
        videoView.start()
        */

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_profile -> {

                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, GestionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
