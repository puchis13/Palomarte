package com.example.palomarapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ReproduccionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reproduccion)

        // Referencias a los elementos de la interfaz
        val numeroNidoInput = findViewById<EditText>(R.id.editTextNumeroNido)
        val madreIdInput = findViewById<EditText>(R.id.editTextPalomaMadreId)
        val padreIdInput = findViewById<EditText>(R.id.editTextPalomaPadreId)
        val fechaCreacionInput = findViewById<EditText>(R.id.editTextFechaCreacionNido)
        val anillosPichonesInput = findViewById<EditText>(R.id.editTextAnillosPichones)
        val cantidadPichonesInput = findViewById<EditText>(R.id.editTextCantidadPichonesLogrados)
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val verListaButton = findViewById<Button>(R.id.verListaButton) // Botón "Ver Lista"

        // Configurar el selector de fecha para fechaCreacionInput
        fechaCreacionInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                val formattedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                fechaCreacionInput.setText(formattedDate)
            }, year, month, day).show()
        }

        // Botón Crear
        createButton.setOnClickListener {
            val params = collectInputs(
                numeroNidoInput, madreIdInput, padreIdInput, fechaCreacionInput,
                anillosPichonesInput, cantidadPichonesInput, statusGroup
            )
            sendHttpRequest("http://192.168.100.2/palomar_api/nidos.php", "POST", params) { response ->
                if (response.has("success")) {
                    Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                    clearFields()
                } else {
                    showError(response)
                }
            }
        }

        // Botón Actualizar
        updateButton.setOnClickListener {
            val params = collectInputs(
                numeroNidoInput, madreIdInput, padreIdInput, fechaCreacionInput,
                anillosPichonesInput, cantidadPichonesInput, statusGroup
            )
            sendHttpRequest("http://192.168.100.2/palomar_api/nidos.php", "PUT", params) { response ->
                if (response.has("success")) {
                    Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                    clearFields()
                } else {
                    showError(response)
                }
            }
        }

        // Botón Eliminar
        deleteButton.setOnClickListener {
            val id = numeroNidoInput.text.toString()
            if (id.isNotEmpty()) {
                sendHttpRequest("http://192.168.100.2/palomar_api/nidos.php?numero_nido=$id", "DELETE", null) { response ->
                    if (response.has("success")) {
                        Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                        clearFields()
                    } else {
                        showError(response)
                    }
                }
            } else {
                Toast.makeText(this, "Ingrese el Número del Nido para eliminar", Toast.LENGTH_LONG).show()
            }
        }

        // Botón Buscar
        searchButton.setOnClickListener {
            val id = numeroNidoInput.text.toString()
            if (id.isNotEmpty()) {
                sendHttpRequest("http://192.168.100.2/palomar_api/nidos.php?numero_nido=$id", "GET", null) { response ->
                    if (response.has("success")) {
                        populateFields(response.getJSONObject("data"))
                        Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                    } else {
                        showError(response)
                    }
                }
            } else {
                Toast.makeText(this, "Ingrese el Número del Nido para buscar", Toast.LENGTH_LONG).show()
            }
        }

        // Botón "Ver Lista"
        verListaButton.setOnClickListener {
            val intent = Intent(this, ListaNidos::class.java) // Abrir la actividad ListaNidos
            startActivity(intent)
        }
    }

    // Colectar datos del formulario
    private fun collectInputs(
        numeroNidoInput: EditText, madreIdInput: EditText, padreIdInput: EditText,
        fechaCreacionInput: EditText, anillosPichonesInput: EditText, cantidadPichonesInput: EditText,
        statusGroup: RadioGroup
    ): HashMap<String, String> {
        return hashMapOf(
            "numero_nido" to numeroNidoInput.text.toString(),
            "paloma_madre_id" to madreIdInput.text.toString(),
            "paloma_padre_id" to padreIdInput.text.toString(),
            "fecha_creacion_nido" to fechaCreacionInput.text.toString(),
            "anillos_pichones" to anillosPichonesInput.text.toString(),
            "cantidad_pichones_logrados" to cantidadPichonesInput.text.toString(),
            "status" to getSelectedStatus(statusGroup)
        )
    }

    // Obtener el estado seleccionado
    private fun getSelectedStatus(group: RadioGroup): String {
        return when (group.checkedRadioButtonId) {
            R.id.radioActivo -> "Activo"
            R.id.radioInactivo -> "Inactivo"
            else -> ""
        }
    }

    // Limpiar los campos del formulario
    private fun clearFields() {
        findViewById<EditText>(R.id.editTextNumeroNido).text.clear()
        findViewById<EditText>(R.id.editTextPalomaMadreId).text.clear()
        findViewById<EditText>(R.id.editTextPalomaPadreId).text.clear()
        findViewById<EditText>(R.id.editTextFechaCreacionNido).text.clear()
        findViewById<EditText>(R.id.editTextAnillosPichones).text.clear()
        findViewById<EditText>(R.id.editTextCantidadPichonesLogrados).text.clear()
        findViewById<RadioGroup>(R.id.radioGroupStatus).clearCheck()
    }

    // Método para enviar peticiones HTTP
    private fun sendHttpRequest(
        url: String, method: String, params: HashMap<String, String>?,
        callback: (JSONObject) -> Unit
    ) {
        object : AsyncTask<Void, Void, Pair<JSONObject?, String?>>() {
            override fun doInBackground(vararg p0: Void?): Pair<JSONObject?, String?> {
                return try {
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
                    val response = urlConnection.inputStream.bufferedReader().use { it.readText() }
                    Pair(JSONObject(response), null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Pair(null, e.message)
                }
            }

            override fun onPostExecute(result: Pair<JSONObject?, String?>) {
                if (result.first != null) {
                    callback(result.first!!)
                } else {
                    Toast.makeText(
                        this@ReproduccionActivity,
                        "Error: ${result.second ?: "Respuesta no válida del servidor"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.execute()
    }

    // Rellenar los campos con datos obtenidos
    private fun populateFields(jsonObject: JSONObject) {
        findViewById<EditText>(R.id.editTextNumeroNido).setText(jsonObject.getString("numero_nido"))
        findViewById<EditText>(R.id.editTextPalomaMadreId).setText(jsonObject.getString("paloma_madre_id"))
        findViewById<EditText>(R.id.editTextPalomaPadreId).setText(jsonObject.getString("paloma_padre_id"))
        findViewById<EditText>(R.id.editTextFechaCreacionNido).setText(jsonObject.getString("fecha_creacion_nido"))
        findViewById<EditText>(R.id.editTextAnillosPichones).setText(jsonObject.getString("anillos_pichones"))
        findViewById<EditText>(R.id.editTextCantidadPichonesLogrados).setText(jsonObject.getString("cantidad_pichones_logrados"))
        val status = jsonObject.getString("status")
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)
        if (status == "Activo") {
            statusGroup.check(R.id.radioActivo)
        } else if (status == "Inactivo") {
            statusGroup.check(R.id.radioInactivo)
        }
    }

    // Mostrar errores
    private fun showError(response: JSONObject) {
        if (response.has("error")) {
            Toast.makeText(this, response.getString("error"), Toast.LENGTH_LONG).show()
        }
    }
}
