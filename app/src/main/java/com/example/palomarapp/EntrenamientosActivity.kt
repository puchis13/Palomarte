package com.example.palomarapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class EntrenamientosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrenamientos)

        val idInput = findViewById<EditText>(R.id.editTextIdEntrenamiento)
        val grupoIdInput = findViewById<EditText>(R.id.editTextGrupoId)
        val clasificacionGroup = findViewById<RadioGroup>(R.id.radioGroupClasificacion)
        val fechaEntrenamientoInput = findViewById<EditText>(R.id.editTextFechaEntrenamiento)
        val ubicacionSueltaInput = findViewById<EditText>(R.id.editTextUbicacionSuelta)
        val kilometrosInput = findViewById<EditText>(R.id.editTextKilometrosAproximados)
        val horaSueltaInput = findViewById<EditText>(R.id.editTextHoraSuelta)
        val horaLlegadaInput = findViewById<EditText>(R.id.editTextHoraLlegada)
        val rankingInput = findViewById<EditText>(R.id.editTextRanking)
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        fechaEntrenamientoInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                fechaEntrenamientoInput.setText("$y-${m + 1}-$d")
            }, year, month, day).show()
        }

        configureTimePicker(horaSueltaInput)
        configureTimePicker(horaLlegadaInput)

        createButton.setOnClickListener {
            val params = collectInputs(
                idInput, grupoIdInput, clasificacionGroup, fechaEntrenamientoInput,
                ubicacionSueltaInput, kilometrosInput, horaSueltaInput,
                horaLlegadaInput, rankingInput, statusGroup
            )
            sendHttpRequest(
                "http://192.168.100.2/palomar_api/entrenamientos.php",
                "POST",
                params,
                "Registro creado con éxito."
            ) {
                clearFields()
            }
        }

        updateButton.setOnClickListener {
            val params = collectInputs(
                idInput, grupoIdInput, clasificacionGroup, fechaEntrenamientoInput,
                ubicacionSueltaInput, kilometrosInput, horaSueltaInput,
                horaLlegadaInput, rankingInput, statusGroup
            )
            sendHttpRequest(
                "http://192.168.100.2/palomar_api/entrenamientos.php",
                "PUT",
                params,
                "Registro actualizado con éxito."
            ) {
                clearFields()
            }
        }

        deleteButton.setOnClickListener {
            val id = idInput.text.toString()
            if (id.isNotEmpty()) {
                sendHttpRequest(
                    "http://192.168.100.2/palomar_api/entrenamientos.php?id=$id",
                    "DELETE",
                    null,
                    "Registro eliminado con éxito."
                ) {
                    clearFields()
                }
            } else {
                Toast.makeText(this, "Ingrese el ID para eliminar", Toast.LENGTH_LONG).show()
            }
        }

        searchButton.setOnClickListener {
            val id = idInput.text.toString()
            if (id.isNotEmpty()) {
                sendHttpRequest(
                    "http://192.168.100.2/palomar_api/entrenamientos.php?id=$id",
                    "GET",
                    null,
                    "Registro encontrado."
                ) {
                    populateFieldsFromServer(it)
                }
            } else {
                Toast.makeText(this, "Ingrese el ID para buscar", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun configureTimePicker(input: EditText) {
        input.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                input.setText(String.format("%02d:%02d:00", h, m))
            }, hour, minute, true).show()
        }
    }

    private fun collectInputs(
        idInput: EditText, grupoIdInput: EditText, clasificacionGroup: RadioGroup,
        fechaEntrenamientoInput: EditText, ubicacionSueltaInput: EditText, kilometrosInput: EditText,
        horaSueltaInput: EditText, horaLlegadaInput: EditText, rankingInput: EditText, statusGroup: RadioGroup
    ): HashMap<String, String> {
        return hashMapOf(
            "id" to idInput.text.toString(),
            "grupo_id" to grupoIdInput.text.toString(),
            "clasificacion_vuelo" to getSelectedRadioValue(clasificacionGroup),
            "fecha_entrenamiento" to fechaEntrenamientoInput.text.toString(),
            "ubicacion_suelta" to ubicacionSueltaInput.text.toString(),
            "kilometros_aproximados" to kilometrosInput.text.toString(),
            "hora_suelta" to horaSueltaInput.text.toString(),
            "hora_llegada" to horaLlegadaInput.text.toString(),
            "ranking" to rankingInput.text.toString(),
            "status" to getSelectedRadioValue(statusGroup)
        )
    }

    private fun getSelectedRadioValue(group: RadioGroup): String {
        val selected = group.checkedRadioButtonId
        return if (selected != -1) findViewById<RadioButton>(selected).text.toString()
        else ""
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.editTextIdEntrenamiento).text.clear()
        findViewById<EditText>(R.id.editTextGrupoId).text.clear()
        findViewById<EditText>(R.id.editTextFechaEntrenamiento).text.clear()
        findViewById<EditText>(R.id.editTextUbicacionSuelta).text.clear()
        findViewById<EditText>(R.id.editTextKilometrosAproximados).text.clear()
        findViewById<EditText>(R.id.editTextHoraSuelta).text.clear()
        findViewById<EditText>(R.id.editTextHoraLlegada).text.clear()
        findViewById<EditText>(R.id.editTextRanking).text.clear()
        findViewById<RadioGroup>(R.id.radioGroupClasificacion).clearCheck()
        findViewById<RadioGroup>(R.id.radioGroupStatus).clearCheck()
    }

    private fun sendHttpRequest(
        url: String,
        method: String,
        params: HashMap<String, String>?,
        successMessage: String,
        onSuccess: ((JSONObject) -> Unit)? = null
    ) {
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
                try {
                    val jsonObject = JSONObject(result)
                    if (jsonObject.has("error")) {
                        Toast.makeText(this@EntrenamientosActivity, jsonObject.getString("error"), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@EntrenamientosActivity, successMessage, Toast.LENGTH_LONG).show()
                        onSuccess?.invoke(jsonObject)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@EntrenamientosActivity, "Error procesando la respuesta", Toast.LENGTH_LONG).show()
                }
            }
        }.execute()
    }

    private fun populateFieldsFromServer(jsonObject: JSONObject) {
        findViewById<EditText>(R.id.editTextIdEntrenamiento).setText(jsonObject.getString("id"))
        findViewById<EditText>(R.id.editTextGrupoId).setText(jsonObject.getString("grupo_id"))
        val clasificacion = jsonObject.getString("clasificacion_vuelo")
        val clasificacionGroup = findViewById<RadioGroup>(R.id.radioGroupClasificacion)
        if (clasificacion == "Ordinarias") {
            clasificacionGroup.check(R.id.radioOrdinarias)
        } else {
            clasificacionGroup.check(R.id.radioDerbys)
        }
        findViewById<EditText>(R.id.editTextFechaEntrenamiento).setText(jsonObject.getString("fecha_entrenamiento"))
        findViewById<EditText>(R.id.editTextUbicacionSuelta).setText(jsonObject.getString("ubicacion_suelta"))
        findViewById<EditText>(R.id.editTextKilometrosAproximados).setText(jsonObject.getString("kilometros_aproximados"))
        findViewById<EditText>(R.id.editTextHoraSuelta).setText(jsonObject.getString("hora_suelta"))
        findViewById<EditText>(R.id.editTextHoraLlegada).setText(jsonObject.getString("hora_llegada"))
        findViewById<EditText>(R.id.editTextRanking).setText(jsonObject.getString("ranking"))
        val status = jsonObject.getString("status")
        val statusGroup = findViewById<RadioGroup>(R.id.radioGroupStatus)
        if (status == "Completado") {
            statusGroup.check(R.id.radioCompletado)
        } else {
            statusGroup.check(R.id.radioPendiente)
        }
    }
}
