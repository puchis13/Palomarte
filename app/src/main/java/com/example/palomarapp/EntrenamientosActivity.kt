package com.example.palomarapp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class EntrenamientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrenamientos)

        val idInput = findViewById<EditText>(R.id.editTextIdEntrenamiento)
        val grupoIdInput = findViewById<EditText>(R.id.editTextGrupoId)
        val clasificacionVueloInput = findViewById<EditText>(R.id.editTextClasificacionVuelo)
        val fechaEntrenamientoInput = findViewById<EditText>(R.id.editTextFechaEntrenamiento)
        val ubicacionSueltaInput = findViewById<EditText>(R.id.editTextUbicacionSuelta)
        val kilometrosInput = findViewById<EditText>(R.id.editTextKilometrosAproximados)
        val horaSueltaInput = findViewById<EditText>(R.id.editTextHoraSuelta)
        val horaLlegadaInput = findViewById<EditText>(R.id.editTextHoraLlegada)
        val rankingInput = findViewById<EditText>(R.id.editTextRanking)
        val statusInput = findViewById<EditText>(R.id.editTextStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        createButton.setOnClickListener {
            val params = hashMapOf(
                "grupo_id" to grupoIdInput.text.toString(),
                "clasificacion_vuelo" to clasificacionVueloInput.text.toString(),
                "fecha_entrenamiento" to fechaEntrenamientoInput.text.toString(),
                "ubicacion_suelta" to ubicacionSueltaInput.text.toString(),
                "kilometros_aproximados" to kilometrosInput.text.toString(),
                "hora_suelta" to horaSueltaInput.text.toString(),
                "hora_llegada" to horaLlegadaInput.text.toString(),
                "ranking" to rankingInput.text.toString(),
                "status" to statusInput.text.toString()
            )
            sendHttpRequest("http://192.168.100.2/palomar/entrenamientos.php", "POST", params)
        }

        updateButton.setOnClickListener {
            val params = hashMapOf(
                "id" to idInput.text.toString(),
                "grupo_id" to grupoIdInput.text.toString(),
                "clasificacion_vuelo" to clasificacionVueloInput.text.toString(),
                "fecha_entrenamiento" to fechaEntrenamientoInput.text.toString(),
                "ubicacion_suelta" to ubicacionSueltaInput.text.toString(),
                "kilometros_aproximados" to kilometrosInput.text.toString(),
                "hora_suelta" to horaSueltaInput.text.toString(),
                "hora_llegada" to horaLlegadaInput.text.toString(),
                "ranking" to rankingInput.text.toString(),
                "status" to statusInput.text.toString()
            )
            sendHttpRequest("http://192.168.100.2/palomar/entrenamientos.php", "PUT", params)
        }

        deleteButton.setOnClickListener {
            val id = idInput.text.toString()
            sendHttpRequest("http://192.168.100.2/palomar/entrenamientos.php?id=$id", "DELETE", null)
        }

        searchButton.setOnClickListener {
            val id = idInput.text.toString()
            sendHttpRequest("http://192.168.100.2/palomar/entrenamientos.php?id=$id", "GET", null)
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
                Toast.makeText(this@EntrenamientosActivity, result, Toast.LENGTH_LONG).show()
            }
        }.execute()
    }
}
