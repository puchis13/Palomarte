package com.example.palomarapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AgregarCompetenciasActivity : AppCompatActivity() {

    private lateinit var nombreClubInput: EditText
    private lateinit var nombreAsociacionInput: EditText
    private lateinit var clasificacionVueloSpinner: Spinner
    private lateinit var ubicacionSueltaInput: EditText
    private lateinit var kilometrosInput: EditText
    private lateinit var fechaCompetenciaTextView: TextView
    private lateinit var horaSueltaTextView: TextView
    private lateinit var rankingInternoInput: EditText
    private lateinit var rankingGeneralInput: EditText
    private lateinit var estadoSpinner: Spinner
    private lateinit var guardarButton: Button

    private var fechaSeleccionada: String? = null
    private var horaSeleccionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_competencias)

        // Vincular vistas
        nombreClubInput = findViewById(R.id.nombreClubInput)
        nombreAsociacionInput = findViewById(R.id.nombreAsociacionInput)
        clasificacionVueloSpinner = findViewById(R.id.clasificacionVueloSpinner)
        ubicacionSueltaInput = findViewById(R.id.ubicacionSueltaInput)
        kilometrosInput = findViewById(R.id.kilometrosInput)
        fechaCompetenciaTextView = findViewById(R.id.fechaCompetenciaTextView)
        horaSueltaTextView = findViewById(R.id.horaSueltaTextView)
        rankingInternoInput = findViewById(R.id.rankingInternoInput)
        rankingGeneralInput = findViewById(R.id.rankingGeneralInput)
        estadoSpinner = findViewById(R.id.estadoSpinner)
        guardarButton = findViewById(R.id.guardarCompetenciaButton)

        // Configurar spinners
        clasificacionVueloSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            arrayOf("Ordinarias", "Derbys")
        )
        estadoSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            arrayOf("Completado", "Pendiente")
        )

        // Configurar selección de fecha
        findViewById<Button>(R.id.fechaCompetenciaButton).setOnClickListener { seleccionarFecha() }

        // Configurar selección de hora
        findViewById<Button>(R.id.horaSueltaButton).setOnClickListener { seleccionarHora() }

        // Configurar botón guardar
        guardarButton.setOnClickListener {
            guardarCompetencia()
        }
    }

    private fun seleccionarFecha() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                fechaSeleccionada = "$year-${month + 1}-$dayOfMonth"
                fechaCompetenciaTextView.text = "Fecha: $fechaSeleccionada"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun seleccionarHora() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hour, minute ->
                horaSeleccionada = "$hour:$minute:00"
                horaSueltaTextView.text = "Hora: $horaSeleccionada"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun guardarCompetencia() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.100.6/palomar_api/add_competencia.php"

        val params = hashMapOf(
            "nombre_club" to nombreClubInput.text.toString(),
            "nombre_asociacion" to nombreAsociacionInput.text.toString(),
            "clasificacion_vuelo" to clasificacionVueloSpinner.selectedItem.toString(),
            "ubicacion_suelta" to ubicacionSueltaInput.text.toString(),
            "kilometros_aproximados" to kilometrosInput.text.toString(),
            "fecha_competencia" to (fechaSeleccionada ?: ""),
            "hora_suelta" to (horaSeleccionada ?: ""),
            "ranking_interno" to rankingInternoInput.text.toString(),
            "ranking_general" to rankingGeneralInput.text.toString(),
            "status" to estadoSpinner.selectedItem.toString()
        )

        val request = object : StringRequest(Request.Method.POST, url,
            {
                Toast.makeText(this, "Competencia guardada", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad
            },
            {
                Toast.makeText(this, "Error al guardar competencia", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams() = params
        }

        queue.add(request)
    }
}
