package com.example.palomarapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CompetenciasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var competenciasAdapter: CompetenciasAdapter
    private lateinit var fabAddCompetencia: FloatingActionButton

    private val GET_COMPETENCIAS_URL = "http://192.168.100.2/palomar_api/competencias.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competencias)

        recyclerView = findViewById(R.id.recyclerViewCompetencias)
        fabAddCompetencia = findViewById(R.id.fabAddCompetencia)

        recyclerView.layoutManager = LinearLayoutManager(this)
        competenciasAdapter = CompetenciasAdapter(arrayListOf())
        recyclerView.adapter = competenciasAdapter

        fetchCompetencias()

        fabAddCompetencia.setOnClickListener {
            val intent = Intent(this, AgregarCompetenciasActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchCompetencias() {
        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, GET_COMPETENCIAS_URL, null,
            { response ->
                val competencias = ArrayList<Competencia>()
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    competencias.add(
                        Competencia(
                            grupoId = jsonObject.getInt("grupo_id"),  // Añadido el campo grupo_id
                            nombreClub = jsonObject.getString("nombre_club"),
                            clasificacionVuelo = jsonObject.getString("clasificacion_vuelo"),
                            ubicacionSuelta = jsonObject.getString("ubicacion_suelta"),
                            fechaCompetencia = jsonObject.getString("fecha_competencia"),
                            status = jsonObject.getString("status"),
                            kilometros = jsonObject.optInt("kilometros_aproximados", 0)
                        )
                    )
                }
                competenciasAdapter.updateData(competencias)
            },
            {
                Toast.makeText(this, "Error al cargar las competencias", Toast.LENGTH_SHORT).show()
            })

        queue.add(jsonArrayRequest)
    }
}
