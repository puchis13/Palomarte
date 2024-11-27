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

class GruposActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos)

        val idInput = findViewById<EditText>(R.id.editTextIdGrupo)
        val nombreGrupoInput = findViewById<EditText>(R.id.editTextNombreGrupo)
        val fechaCreacionInput = findViewById<EditText>(R.id.editTextFechaCreacion)
        val totalPalomasInput = findViewById<EditText>(R.id.editTextTotalPalomas)
        val clasificacionGroup = findViewById<RadioGroup>(R.id.radioGroupClasificacion)
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val viewListButton = findViewById<Button>(R.id.button) // Botón "Ver lista"

        // Selector de fecha para el campo de creación
        fechaCreacionInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                fechaCreacionInput.setText(selectedDate)
            }, year, month, day)
            datePicker.show()
        }

        // Botón Crear
        createButton.setOnClickListener {
            val clasificacion = getSelectedRadioValue(clasificacionGroup)
            val status = getSelectedRadioValue(statusGroup)
            val params = hashMapOf(
                "nombre_grupo" to nombreGrupoInput.text.toString(),
                "fecha_creacion_grupo" to fechaCreacionInput.text.toString(),
                "total_palomas" to totalPalomasInput.text.toString(),
                "clasificacion" to clasificacion,
                "status" to status
            )
            sendHttpRequest("http://192.168.100.2/palomar_api/grupos.php", "POST", params) { response ->
                Toast.makeText(this, "Grupo creado con éxito", Toast.LENGTH_LONG).show()
                clearFields(idInput, nombreGrupoInput, fechaCreacionInput, totalPalomasInput)
                clasificacionGroup.clearCheck()
                statusGroup.clearCheck()
            }
        }

        // Botón Actualizar
        updateButton.setOnClickListener {
            val clasificacion = getSelectedRadioValue(clasificacionGroup)
            val status = getSelectedRadioValue(statusGroup)
            val params = hashMapOf(
                "id" to idInput.text.toString(),
                "nombre_grupo" to nombreGrupoInput.text.toString(),
                "fecha_creacion_grupo" to fechaCreacionInput.text.toString(),
                "total_palomas" to totalPalomasInput.text.toString(),
                "clasificacion" to clasificacion,
                "status" to status
            )
            sendHttpRequest("http://192.168.100.2/palomar_api/grupos.php", "PUT", params) { response ->
                Toast.makeText(this, "Grupo actualizado con éxito", Toast.LENGTH_LONG).show()
                clearFields(idInput, nombreGrupoInput, fechaCreacionInput, totalPalomasInput)
                clasificacionGroup.clearCheck()
                statusGroup.clearCheck()
            }
        }

        // Botón Buscar
        searchButton.setOnClickListener {
            val id = idInput.text.toString()
            if (id.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa un ID válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendHttpRequest("http://192.168.100.2/palomar_api/grupos.php?id=$id", "GET", null) { response ->
                val grupo = JSONObject(response).getJSONObject("data")
                nombreGrupoInput.setText(grupo.getString("nombre_grupo"))
                fechaCreacionInput.setText(grupo.getString("fecha_creacion_grupo"))
                totalPalomasInput.setText(grupo.getString("total_palomas"))

                when (grupo.getString("clasificacion")) {
                    "Ordinarias" -> clasificacionGroup.check(R.id.radioOrdinarias)
                    "Derbys" -> clasificacionGroup.check(R.id.radioDerbys)
                }

                when (grupo.getString("status")) {
                    "Activo" -> statusGroup.check(R.id.radioActivo)
                    "Inactivo" -> statusGroup.check(R.id.radioInactivo)
                }
            }
        }

        // Botón Eliminar
        deleteButton.setOnClickListener {
            val id = idInput.text.toString()
            if (id.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa un ID válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendHttpRequest("http://192.168.100.2/palomar_api/grupos.php?id=$id", "DELETE", null) {
                Toast.makeText(this, "Grupo eliminado con éxito", Toast.LENGTH_LONG).show()
                clearFields(idInput, nombreGrupoInput, fechaCreacionInput, totalPalomasInput)
                clasificacionGroup.clearCheck()
                statusGroup.clearCheck()
            }
        }

        // Botón Ver lista
        viewListButton.setOnClickListener {
            val intent = Intent(this, ListaGrupos::class.java)
            startActivity(intent)
        }
    }

    private fun getSelectedRadioValue(group: RadioGroup): String {
        val selected = group.checkedRadioButtonId
        return if (selected != -1) findViewById<RadioButton>(selected).text.toString()
        else throw IllegalArgumentException("Selecciona un valor")
    }

    private fun sendHttpRequest(url: String, method: String, params: HashMap<String, String>?, callback: (String) -> Unit) {
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
                callback(result)
            }
        }.execute()
    }

    private fun clearFields(vararg fields: EditText) {
        for (field in fields) {
            field.text.clear()
        }
    }
}
