package com.example.palomarapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // Inicializar vistas
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)
        logoutButton = findViewById(R.id.logoutButton)

        // Obtener datos del usuario del Intent
        val userName = intent.getStringExtra("userName")
        val userEmail = intent.getStringExtra("userEmail")

        // Mostrar los datos en los TextViews
        userNameTextView.text = "Usuario: $userName"
        userEmailTextView.text = "Correo: $userEmail"

        // Configurar el botón de cerrar sesión
        logoutButton.setOnClickListener {
            // Regresar al LoginActivity y limpiar las actividades en el stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
