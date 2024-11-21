package com.example.palomarapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    // Declaración de variables para vincular con los elementos del XML
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button

    // URL del endpoint PHP
    private val LOGIN_URL = "http://192.168.100.2/palomar/login.php" // Cambia esto si usas una IP diferente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Vincular variables con elementos del XML
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        // Configurar el botón de inicio de sesión
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Por favor ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para autenticar al usuario
    private fun loginUser(username: String, password: String) {
        val queue: RequestQueue = Volley.newRequestQueue(this)

        // Parámetros de la solicitud
        val params = HashMap<String, String>()
        params["username"] = username
        params["password"] = password

        val jsonObject = JSONObject(params as Map<*, *>)

        // Configuración de la solicitud HTTP POST
        val request = JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonObject,
            Response.Listener { response ->
                val success = response.getBoolean("success")
                if (success) {
                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()

                    // Redirigir al MainActivity después de un login exitoso
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Finaliza LoginActivity para que no se pueda regresar
                } else {
                    val message = response.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la conexión al servidor", Toast.LENGTH_SHORT).show()
            })

        // Agregar la solicitud a la cola
        queue.add(request)
    }
}
