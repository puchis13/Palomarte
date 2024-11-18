package com.example.palomarapp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class ReproduccionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reproduccion)

        val idInput = findViewById<EditText>(R.id.editTextIdReproduccion)
        val fechaCreacionInput = findViewById<EditText>(R.id.editTextFechaCreacionNido)
        val numeroNidoInput = findViewById<EditText>(R.id.editTextNumeroNido)
        val madreIdInput = findViewById<EditText>(R.id.editTextPalomaMadreId)
        val padreIdInput = findViewById<EditText>(R.id.editTextPalomaPadreId)
        val anilloPichonInput = findViewById<EditText>(R.id.editTextAnilloPichon)
        val cantidadPichonesInput = findViewById<EditText>(R.id.editTextCantidadPichonesLogrados)
        val statusInput = findViewById<EditText>(R.id.editTextStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        createButton.setOnClickListener {
            val params = hashMapOf(
                "fecha_creacion_nido" to fechaCreacionInput.text.toString(),
                "numero_nido" to numeroNidoInput.text.toString(),
                "paloma_madre_id" to madreIdInput.text.toString(),
                "paloma_padre_id" to padreIdInput.text.toString(),
                "anillo_pichon" to anilloPichonInput.text.toString(),
                "cantidad_pichones_logrados" to cantidadPichonesInput.text.toString(),
                "status" to statusInput.text.toString()
            )
            sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php", "POST", params)
        }

        updateButton.setOnClickListener {
            val params = hashMapOf(
                "id" to idInput.text.toString(),
                "fecha_creacion_nido" to fechaCreacionInput.text.toString(),
                "numero_nido" to numeroNidoInput.text.toString(),
                "paloma_madre_id" to madreIdInput.text.toString(),
                "paloma_padre_id" to padreIdInput.text.toString(),
                "anillo_pichon" to anilloPichonInput.text.toString(),
                "cantidad_pichones_logrados" to cantidadPichonesInput.text.toString(),
                "status" to statusInput.text.toString()
            )
            sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php", "PUT", params)
        }

        deleteButton.setOnClickListener {
            val id = idInput.text.toString()
            sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php?id=$id", "DELETE", null)
        }

        searchButton.setOnClickListener {
            val id = idInput.text.toString()
            sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php?id=$id", "GET", null)
        }
    }

    private fun sendHttpRequest(url: String, method: String, params: HashMap<String, String>?) {
        object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg p0: Void?): String {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.requestMethod = method
                if (params != null && (method == "POST" || method == "PUT")) {
                    urlConnection.doOutput = true
                    val out = urlConnection.outputStream
                    val postData = params.map { "${it.key}=${it.value}" }.joinToString("&")
                    out.write(postData.toByteArray())
                    out.flush()
                    out.close()
                }
                return urlConnection.inputStream.bufferedReader().use { it.readText() }
            }

            override fun onPostExecute(result: String) {
                Toast.makeText(this@ReproduccionActivity, result, Toast.LENGTH_LONG).show()
            }
        }.execute()
    }
}
