package com.example.palomarapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class PalomaActivity : AppCompatActivity() {

    // Declarar los campos de entrada
    private lateinit var idInput: EditText
    private lateinit var aliasInput: EditText
    private lateinit var fechaNacimientoInput: EditText
    private lateinit var clasificacionAnilloInput: EditText
    private lateinit var sexoInput: EditText
    private lateinit var colorPlumajeInput: EditText
    private lateinit var madreIdInput: EditText
    private lateinit var padreIdInput: EditText
    private lateinit var razaInput: EditText
    private lateinit var capacidadVueloInput: EditText
    private lateinit var statusInput: EditText
    private lateinit var createButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    // URLs del servidor
    private val insertUrl = "http://192.168.100.6/palomar_api/insert_paloma.php"
    private val updateUrl = "http://192.168.100.6/palomar_api/update_paloma.php"
    private val deleteUrl = "http://192.168.100.6/palomar_api/delete_paloma.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paloma)

        // Inicializar los campos de entrada
        idInput = findViewById(R.id.idInput)
        aliasInput = findViewById(R.id.aliasInput)
        fechaNacimientoInput = findViewById(R.id.fechaNacimientoInput)
        clasificacionAnilloInput = findViewById(R.id.clasificacionAnilloInput)
        sexoInput = findViewById(R.id.sexoInput)
        colorPlumajeInput = findViewById(R.id.colorPlumajeInput)
        madreIdInput = findViewById(R.id.madreIdInput)
        padreIdInput = findViewById(R.id.padreIdInput)
        razaInput = findViewById(R.id.razaInput)
        capacidadVueloInput = findViewById(R.id.capacidadVueloInput)
        statusInput = findViewById(R.id.statusInput)
        createButton = findViewById(R.id.createButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)

        // Configurar botones
        createButton.setOnClickListener { insertPaloma() }
        updateButton.setOnClickListener { updatePaloma() }
        deleteButton.setOnClickListener { deletePaloma() }
    }

    private fun insertPaloma() {
        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>().apply {
            put("id", idInput.text.toString())
            put("alias", aliasInput.text.toString())
            put("fecha_nacimiento", fechaNacimientoInput.text.toString())
            put("clasificacion_anillo", clasificacionAnilloInput.text.toString())
            put("sexo", sexoInput.text.toString())
            put("color_plumaje", colorPlumajeInput.text.toString())
            put("madre_id", madreIdInput.text.toString())
            put("padre_id", padreIdInput.text.toString())
            put("raza", razaInput.text.toString())
            put("capacidad_vuelo", capacidadVueloInput.text.toString())
            put("status", statusInput.text.toString())
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, insertUrl,
            Response.Listener { response ->
                Log.d("InsertResponse", response)
                Toast.makeText(this, "Paloma creada correctamente", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Log.e("InsertError", error.toString())
                Toast.makeText(this, "Error al crear la paloma", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = params
        }

        queue.add(stringRequest)
    }

    private fun updatePaloma() {
        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>().apply {
            put("id", idInput.text.toString()) // El ID debe ser obligatorio para editar
            put("alias", aliasInput.text.toString())
            put("fecha_nacimiento", fechaNacimientoInput.text.toString())
            put("clasificacion_anillo", clasificacionAnilloInput.text.toString())
            put("sexo", sexoInput.text.toString())
            put("color_plumaje", colorPlumajeInput.text.toString())
            put("madre_id", madreIdInput.text.toString())
            put("padre_id", padreIdInput.text.toString())
            put("raza", razaInput.text.toString())
            put("capacidad_vuelo", capacidadVueloInput.text.toString())
            put("status", statusInput.text.toString())
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, updateUrl,
            Response.Listener { response ->
                Log.d("UpdateResponse", response)
                Toast.makeText(this, "Paloma actualizada correctamente", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Log.e("UpdateError", error.toString())
                Toast.makeText(this, "Error al actualizar la paloma", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = params
        }

        queue.add(stringRequest)
    }

    private fun deletePaloma() {
        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>().apply {
            put("id", idInput.text.toString()) // Solo se necesita el ID para eliminar
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, deleteUrl,
            Response.Listener { response ->
                Log.d("DeleteResponse", response)
                Toast.makeText(this, "Paloma eliminada correctamente", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Log.e("DeleteError", error.toString())
                Toast.makeText(this, "Error al eliminar la paloma", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = params
        }

        queue.add(stringRequest)
    }
}
