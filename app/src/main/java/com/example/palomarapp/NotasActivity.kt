package com.example.palomarapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class NotasActivity : AppCompatActivity() {

    private lateinit var tituloInput: EditText
    private lateinit var contenidoInput: EditText
    private lateinit var prioridadSpinner: Spinner
    private lateinit var guardarButton: Button
    private lateinit var fechaButton: Button
    private lateinit var fechaTextView: TextView
    private lateinit var notasListView: ListView

    private var selectedDate: String? = null // Fecha seleccionada por el usuario

    // URL de la API
    private val GET_NOTAS_URL = "http://192.168.100.6/palomar_api/get_notas.php"
    private val POST_NOTAS_URL = "http://192.168.100.6/palomar_api/add_nota.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        // Vincular vistas
        tituloInput = findViewById(R.id.tituloInput)
        contenidoInput = findViewById(R.id.contenidoInput)
        prioridadSpinner = findViewById(R.id.prioridadSpinner)
        guardarButton = findViewById(R.id.guardarButton)
        fechaButton = findViewById(R.id.fechaButton)
        fechaTextView = findViewById(R.id.fechaTextView)
        notasListView = findViewById(R.id.notasListView)

        // Configurar spinner de prioridades
        val prioridades = arrayOf("Alta", "Media", "Baja")
        prioridadSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prioridades)

        // Configurar botón para seleccionar fecha
        fechaButton.setOnClickListener { seleccionarFecha() }

        // Configurar botón guardar
        guardarButton.setOnClickListener {
            val titulo = tituloInput.text.toString()
            val contenido = contenidoInput.text.toString()
            val prioridad = prioridadSpinner.selectedItem.toString()

            if (titulo.isNotEmpty() && contenido.isNotEmpty() && selectedDate != null) {
                guardarNota(titulo, contenido, prioridad, selectedDate!!)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos y selecciona una fecha", Toast.LENGTH_SHORT).show()
            }
        }

        // Cargar notas al inicio
        cargarNotas()
    }

    private fun seleccionarFecha() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = Calendar.getInstance()
                date.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0) // Hora fija 00:00:00

                selectedDate = sdf.format(date.time)
                fechaTextView.text = "Fecha: $selectedDate"
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun cargarNotas() {
        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, GET_NOTAS_URL, null,
            Response.Listener { response ->
                val notas = ArrayList<String>()

                for (i in 0 until response.length()) {
                    val nota = response.getJSONObject(i)
                    val titulo = nota.getString("titulo")
                    val contenido = nota.getString("contenido")
                    val prioridad = nota.getString("prioridad")
                    val fecha = nota.getString("fecha")

                    notas.add("Título: $titulo\nContenido: $contenido\nPrioridad: $prioridad\nFecha: $fecha")
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notas)
                notasListView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Log.e("NotasError", "Error al cargar notas: ${error.message}")
                Toast.makeText(this, "Error al cargar notas", Toast.LENGTH_SHORT).show()
            })

        queue.add(jsonArrayRequest)
    }

    private fun guardarNota(titulo: String, contenido: String, prioridad: String, fecha: String) {
        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>()
        params["titulo"] = titulo
        params["contenido"] = contenido
        params["prioridad"] = prioridad
        params["fecha"] = fecha

        val jsonObject = JSONObject(params as Map<*, *>)

        val stringRequest = object : StringRequest(Request.Method.POST, POST_NOTAS_URL,
            Response.Listener {
                Toast.makeText(this, "Nota guardada correctamente", Toast.LENGTH_SHORT).show()
                // Limpiar campos
                tituloInput.text.clear()
                contenidoInput.text.clear()
                prioridadSpinner.setSelection(1) // Volver a "Media"
                fechaTextView.text = "Fecha: No seleccionada"
                selectedDate = null
                // Recargar notas
                cargarNotas()
            },
            Response.ErrorListener { error ->
                Log.e("GuardarNotaError", "Error al guardar nota: ${error.message}")
                Toast.makeText(this, "Error al guardar nota", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> = params
        }

        queue.add(stringRequest)
    }
}
