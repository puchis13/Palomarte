package com.example.palomarapp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class GruposActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos)

        val idInput = findViewById<EditText>(R.id.editTextIdGrupo)
        val nombreGrupoInput = findViewById<EditText>(R.id.editTextNombreGrupo)
        val fechaCreacionInput = findViewById<EditText>(R.id.editTextFechaCreacion)
        val totalPalomasInput = findViewById<EditText>(R.id.editTextTotalPalomas)
        val clasificacionInput = findViewById<EditText>(R.id.editTextClasificacion)
        val statusInput = findViewById<EditText>(R.id.editTextStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        createButton.setOnClickListener {
            val params = hashMapOf(
                "nombre_grupo" to nombreGrupoInput.text.toString(),
                "fecha_creacion_grupo" to fechaCreacionInput.text.toString(),
                "total_palomas" to totalPalomasInput.text.toString(),
                "clasificacion" to clasificacionInput.text.toString(),
                "status" to statusInput.text.toString()
            )
            sendHttpRequest("http://192.168.100.2/palomar/grupos.php", "POST", params)
        }

        updateButton.setOnClickListener {
            val params = hashMapOf(
                "id" to idInput.text.toString(),
                "nombre_grupo" to nombreGrupoInput.text.toString(),
                "fecha_creacion_grupo" to fechaCreacionInput.text.toString(),
                "total_palomas" to totalPalomasInput.text.toString(),
                "clasificacion" to clasificacionInput.text.toString(),
                "status" to statusInput.text.toString()
            )
            sendHttpRequest("http://192.168.100.2/palomar/grupos.php", "PUT", params)
        }

        deleteButton.setOnClickListener {
            val id = idInput.text.toString()
            sendHttpRequest("http://192.168.100.2/palomar/grupos.php?id=$id", "DELETE", null)
        }

        searchButton.setOnClickListener {
            val id = idInput.text.toString()
            sendHttpRequest("http://192.168.100.2/palomar/grupos.php?id=$id", "GET", null)
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
                Toast.makeText(this@GruposActivity, result, Toast.LENGTH_LONG).show()
            }
        }.execute()
    }
}
