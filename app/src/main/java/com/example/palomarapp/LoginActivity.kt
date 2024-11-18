package com.example.palomarapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var forgotPasswordText: TextView
    private lateinit var registerText: TextView

    private val LOGIN_URL = "http://192.168.100.6/palomar_api/login.php" // Cambia localhost a tu IP si usas un dispositivo físico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        registerText = findViewById(R.id.registerText)

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

        // Configurar el texto de registro para redirigir a la actividad de registro
        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Configurar el texto de "¿Olvidaste tu contraseña?"
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de recuperación de contraseña no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(username: String, password: String) {
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>()
        params["username"] = username
        params["password"] = password

        val jsonObject = JSONObject(params as Map<*, *>)

        val request = JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonObject,
            Response.Listener { response ->
                val success = response.getBoolean("success")
                if (success) {
                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()

                    // Redirigir a MainActivity después de un login exitoso
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Opcional: Finaliza LoginActivity para que el usuario no pueda regresar con el botón de retroceso
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error en la conexión", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }
}
