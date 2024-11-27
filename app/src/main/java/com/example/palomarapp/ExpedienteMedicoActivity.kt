package com.example.palomarapp

import Hospital
import HospitalAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class ExpedienteMedicoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hospitalAdapter: HospitalAdapter // Adaptador para RecyclerView

    private val GET_HOSPITALS_URL = "http://192.168.100.6/palomar_api/get_hospitals.php"  // URL del API para hospitales

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expediente_medico)

        recyclerView = findViewById(R.id.recyclerViewHospitals)
        recyclerView.layoutManager = LinearLayoutManager(this)
        hospitalAdapter = HospitalAdapter(arrayListOf())  // Adaptador vacío inicialmente
        recyclerView.adapter = hospitalAdapter

        fetchHospitals()  // Llamada para obtener los hospitales
    }

    private fun fetchHospitals() {
        val queue = Volley.newRequestQueue(this)

        // Usamos JsonArrayRequest ya que esperamos un array de hospitales
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, GET_HOSPITALS_URL, null,
            { response ->
                // Depuración para ver el JSON
                Log.d("Hospitals", "JSON Response: $response")

                val hospitals = ArrayList<Hospital>()
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)

                    hospitals.add(
                        Hospital(
                            id = jsonObject.getString("id"),
                            fechaSintomas = jsonObject.getString("fecha_sintomas"),
                            palomaId = jsonObject.getString("paloma_id"),
                            descripcionEnfermedad = jsonObject.getString("descripcion_enfermedad"),
                            medicacion = jsonObject.getString("medicacion"),
                            status = jsonObject.getString("status")
                        )
                    )
                }

                // Actualizamos los datos en el adaptador
                hospitalAdapter.updateData(hospitals)
            },
            {
                Log.e("FetchError", "Error al obtener hospitales: ${it.message}")
                Toast.makeText(this, "Error al cargar hospitales", Toast.LENGTH_SHORT).show()
            })

        queue.add(jsonArrayRequest)
    }
}
