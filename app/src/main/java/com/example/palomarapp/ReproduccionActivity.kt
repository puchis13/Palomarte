package com.example.palomarapp

import android.app.DatePickerDialog
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

        val idInput = findViewById<EditText>(R.id.editTextIdReproduccion)
        val fechaCreacionInput = findViewById<EditText>(R.id.editTextFechaCreacionNido)
        val numeroNidoInput = findViewById<EditText>(R.id.editTextNumeroNido)
        val madreIdInput = findViewById<EditText>(R.id.editTextPalomaMadreId)
        val padreIdInput = findViewById<EditText>(R.id.editTextPalomaPadreId)
        val anilloPichonInput = findViewById<EditText>(R.id.editTextAnilloPichon)
        val cantidadPichonesInput = findViewById<EditText>(R.id.editTextCantidadPichonesLogrados)
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        // Configurar selector de fecha
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

        createButton.setOnClickListener {
            val params = collectInputs(
                idInput, fechaCreacionInput, numeroNidoInput, madreIdInput,
                padreIdInput, anilloPichonInput, cantidadPichonesInput, statusGroup
            )
            sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php", "POST", params) { response ->
                if (response.has("success")) {
                    Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                    clearFields()
                } else {
                    showError(response)
                }
            }
        }

        updateButton.setOnClickListener {
            val params = collectInputs(
                idInput, fechaCreacionInput, numeroNidoInput, madreIdInput,
                padreIdInput, anilloPichonInput, cantidadPichonesInput, statusGroup
            )
            sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php", "PUT", params) { response ->
                if (response.has("success")) {
                    Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                    clearFields()
                } else {
                    showError(response)
                }
            }
        }

        deleteButton.setOnClickListener {
            val id = idInput.text.toString()
            if (id.isNotEmpty()) {
                sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php?id=$id", "DELETE", null) { response ->
                    if (response.has("success")) {
                        Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                        clearFields()
                    } else {
                        showError(response)
                    }
                }
            } else {
                Toast.makeText(this, "Ingrese el ID para eliminar", Toast.LENGTH_LONG).show()
            }
        }

        searchButton.setOnClickListener {
            val id = idInput.text.toString()
            if (id.isNotEmpty()) {
                sendHttpRequest("http://192.168.100.2/palomar/reproduccions.php?id=$id", "GET", null) { response ->
                    if (response.has("success")) {
                        populateFields(response.getJSONObject("data"))
                        Toast.makeText(this, response.getString("success"), Toast.LENGTH_LONG).show()
                    } else {
                        showError(response)
                    }
                }
            } else {
                Toast.makeText(this, "Ingrese el ID para buscar", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun collectInputs(
        idInput: EditText, fechaCreacionInput: EditText, numeroNidoInput: EditText,
        madreIdInput: EditText, padreIdInput: EditText, anilloPichonInput: EditText,
        cantidadPichonesInput: EditText, statusGroup: RadioGroup
    ): HashMap<String, String> {
        return hashMapOf(
            "id" to idInput.text.toString(),
            "fecha_creacion_nido" to fechaCreacionInput.text.toString(),
            "numero_nido" to numeroNidoInput.text.toString(),
            "paloma_madre_id" to madreIdInput.text.toString(),
            "paloma_padre_id" to padreIdInput.text.toString(),
            "anillo_pichon" to anilloPichonInput.text.toString(),
            "cantidad_pichones_logrados" to cantidadPichonesInput.text.toString(),
            "status" to getSelectedStatus(statusGroup)
        )
    }

    private fun getSelectedStatus(group: RadioGroup): String {
        return when (group.checkedRadioButtonId) {
            R.id.radioActivo -> "Activo"
            R.id.radioInactivo -> "Inactivo"
            else -> ""
        }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.editTextIdReproduccion).text.clear()
        findViewById<EditText>(R.id.editTextFechaCreacionNido).text.clear()
        findViewById<EditText>(R.id.editTextNumeroNido).text.clear()
        findViewById<EditText>(R.id.editTextPalomaMadreId).text.clear()
        findViewById<EditText>(R.id.editTextPalomaPadreId).text.clear()
        findViewById<EditText>(R.id.editTextAnilloPichon).text.clear()
        findViewById<EditText>(R.id.editTextCantidadPichonesLogrados).text.clear()
        findViewById<RadioGroup>(R.id.radioGroupStatus).clearCheck()
    }

    private fun sendHttpRequest(
        url: String, method: String, params: HashMap<String, String>?,
        callback: (JSONObject) -> Unit
    ) {
        object : AsyncTask<Void, Void, JSONObject>() {
            override fun doInBackground(vararg p0: Void?): JSONObject {
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
                return JSONObject(response)
            }

            override fun onPostExecute(result: JSONObject) {
                callback(result)
            }
        }.execute()
    }

    private fun populateFields(jsonObject: JSONObject) {
        findViewById<EditText>(R.id.editTextIdReproduccion).setText(jsonObject.getString("id"))
        findViewById<EditText>(R.id.editTextFechaCreacionNido).setText(jsonObject.getString("fecha_creacion_nido"))
        findViewById<EditText>(R.id.editTextNumeroNido).setText(jsonObject.getString("numero_nido"))
        findViewById<EditText>(R.id.editTextPalomaMadreId).setText(jsonObject.getString("paloma_madre_id"))
        findViewById<EditText>(R.id.editTextPalomaPadreId).setText(jsonObject.getString("paloma_padre_id"))
        findViewById<EditText>(R.id.editTextAnilloPichon).setText(jsonObject.getString("anillo_pichon"))
        findViewById<EditText>(R.id.editTextCantidadPichonesLogrados).setText(jsonObject.getString("cantidad_pichones_logrados"))
        val status = jsonObject.getString("status")
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)
        if (status == "Activo") {
            statusGroup.check(R.id.radioActivo)
        } else if (status == "Inactivo") {
            statusGroup.check(R.id.radioInactivo)
        }
    }

    private fun showError(response: JSONObject) {
        if (response.has("error")) {
            Toast.makeText(this, response.getString("error"), Toast.LENGTH_LONG).show()
        }
    }
}
