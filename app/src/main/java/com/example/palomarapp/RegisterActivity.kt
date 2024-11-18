package com.example.palomarapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonRegister: Button
    private val REGISTER_URL = "http://192.168.100.2/palomar_api/register.php" // Cambia a tu IP si estás en un dispositivo físico

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonRegister = findViewById(R.id.buttonRegister)

        // Configurar el botón de registro
        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, email, password)
            } else {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        val queue: RequestQueue = Volley.newRequestQueue(this)

        // Crear los datos JSON para enviar
        val params = HashMap<String, String>()
        params["username"] = username
        params["email"] = email
        params["password"] = password

        val jsonObject = JSONObject(params as Map<*, *>)

        Log.d("RegisterDebug", "Enviando solicitud a $REGISTER_URL con datos: $jsonObject")

        val request = JsonObjectRequest(
            Request.Method.POST,
            REGISTER_URL,
            jsonObject,
            Response.Listener { response ->
                val success = response.getBoolean("success")
                if (success) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    // Redirigir a LoginActivity después del registro exitoso
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    val message = response.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    Log.d("RegisterDebug", "Error en el registro: $message")
                }
            },
            Response.ErrorListener { error ->
                Log.e("RegisterError", "Error en la conexión: ${error.message}")
                Toast.makeText(this, "Error en la conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(request)
    }
}
