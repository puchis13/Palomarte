package com.example.palomarapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recuperar datos enviados desde LoginActivity
        val userName = intent.getStringExtra("userName") // Recupera el nombre
        val userEmail = intent.getStringExtra("userEmail") // Recupera el correo





        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Configuración del BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Acción para la opción "Home"
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    // Acción para la opción "Profile"
                    val intent = Intent(this, UserDetailsActivity::class.java)
                    intent.putExtra("userName", userName)
                    intent.putExtra("userEmail", userEmail)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    // Acción para la opción "Settings"
                    startActivity(Intent(this, GestionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
