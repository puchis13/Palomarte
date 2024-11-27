package com.example.palomarapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AgregarCompetenciasActivity : AppCompatActivity() {

    private lateinit var grupoIdInput: EditText
    private lateinit var nombreClubInput: EditText
    private lateinit var nombreAsociacionInput: EditText
    private lateinit var clasificacionVueloSpinner: Spinner
    private lateinit var ubicacionSueltaInput: EditText
    private lateinit var kilometrosInput: EditText
    private lateinit var fechaCompetenciaTextView: TextView
    private lateinit var horaSueltaTextView: TextView
    private lateinit var estadoSpinner: Spinner
    private lateinit var guardarButton: Button

    private var fechaSeleccionada: String = ""
    private var horaSeleccionada: String = ""

    private val ADD_COMPETENCIA_URL = "http://192.168.100.2/palomar_api/insertar_competencia.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_competencias)

        // Enlaces a los componentes
        grupoIdInput = findViewById(R.id.grupoIdInput)
        nombreClubInput = findViewById(R.id.nombreClubInput)
        nombreAsociacionInput = findViewById(R.id.nombreAsociacionInput)
        clasificacionVueloSpinner = findViewById(R.id.clasificacionVueloSpinner)
        ubicacionSueltaInput = findViewById(R.id.ubicacionSueltaInput)
        kilometrosInput = findViewById(R.id.kilometrosInput)
        fechaCompetenciaTextView = findViewById(R.id.fechaCompetenciaTextView)
        horaSueltaTextView = findViewById(R.id.horaSueltaTextView)
        estadoSpinner = findViewById(R.id.estadoSpinner)
        guardarButton = findViewById(R.id.guardarCompetenciaButton)

        guardarButton.setOnClickListener {
            val grupoId = grupoIdInput.text.toString()
            val nombreClub = nombreClubInput.text.toString()
            val nombreAsociacion = nombreAsociacionInput.text.toString()
            val clasificacionVuelo = clasificacionVueloSpinner.selectedItem.toString()
            val ubicacionSuelta = ubicacionSueltaInput.text.toString()
            val kilometros = kilometrosInput.text.toString()
            val fechaCompetencia = fechaCompetenciaTextView.text.toString()
            val horaSuelta = horaSueltaTextView.text.toString()
            val estado = estadoSpinner.selectedItem.toString()

            if (grupoId.isEmpty() || nombreClub.isEmpty() || ubicacionSuelta.isEmpty() || kilometros.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            } else {
                insertarCompetencia(grupoId, nombreClub, nombreAsociacion, clasificacionVuelo, ubicacionSuelta, kilometros, fechaCompetencia, horaSuelta, estado)
            }
        }
    }

    private fun insertarCompetencia(grupoId: String, nombreClub: String, nombreAsociacion: String, clasificacionVuelo: String, ubicacionSuelta: String, kilometros: String, fechaCompetencia: String, horaSuelta: String, estado: String) {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Request.Method.POST, ADD_COMPETENCIA_URL,
            { response ->
                Toast.makeText(this, "Competencia agregada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Error al agregar competencia", Toast.LENGTH_SHORT).show()
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["grupo_id"] = grupoId
                params["nombre_club"] = nombreClub
                params["nombre_asociacion"] = nombreAsociacion
                params["clasificacion_vuelo"] = clasificacionVuelo
                params["ubicacion_suelta"] = ubicacionSuelta
                params["kilometros"] = kilometros
                params["fecha_competencia"] = fechaCompetencia
                params["hora_suelta"] = horaSuelta
                params["estado"] = estado
                return params
            }
        }
        queue.add(stringRequest)
    }
}
