package com.example.palomarapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ListaGrupos : AppCompatActivity() {
    private lateinit var gruposContainer: LinearLayout
    private lateinit var noDataTextView: TextView

    private val getGruposUrl = "http://192.168.100.2/palomar_api/grupos.php?action=ver_lista"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_grupos)

        gruposContainer = findViewById(R.id.gruposContainer)
        noDataTextView = findViewById(R.id.noDataTextView)

        fetchGrupos()
    }

    private fun fetchGrupos() {
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, getGruposUrl, null,
            Response.Listener { response ->
                try {
                    val gruposArray = response.getJSONArray("data")
                    if (gruposArray.length() > 0) {
                        noDataTextView.visibility = View.GONE
                        gruposContainer.visibility = View.VISIBLE

                        for (i in 0 until gruposArray.length()) {
                            val grupo = gruposArray.getJSONObject(i)

                            val id = grupo.getString("id")
                            val nombre = grupo.getString("nombre_grupo")
                            val fechaCreacion = grupo.getString("fecha_creacion_grupo")
                            val totalPalomas = grupo.getString("total_palomas")
                            val clasificacion = grupo.getString("clasificacion")
                            val status = grupo.getString("status")

                            val grupoView = createGrupoView(
                                id,
                                nombre,
                                fechaCreacion,
                                totalPalomas,
                                clasificacion,
                                status
                            )

                            gruposContainer.addView(grupoView)
                        }
                    } else {
                        noDataTextView.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    Log.e("ParseError", e.toString())
                    noDataTextView.text = "Error al procesar los datos."
                    noDataTextView.visibility = View.VISIBLE
                }
            },
            Response.ErrorListener { error ->
                Log.e("VolleyError", error.toString())
                noDataTextView.text = "Error al obtener los grupos."
                noDataTextView.visibility = View.VISIBLE
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun createGrupoView(
        id: String,
        nombre: String,
        fechaCreacion: String,
        totalPalomas: String,
        clasificacion: String,
        status: String
    ): View {
        val view = layoutInflater.inflate(R.layout.grupo_item, null)

        val idTextView: TextView = view.findViewById(R.id.idTextView)
        val nombreTextView: TextView = view.findViewById(R.id.nombreTextView)
        val fechaCreacionTextView: TextView = view.findViewById(R.id.fechaCreacionTextView)
        val totalPalomasTextView: TextView = view.findViewById(R.id.totalPalomasTextView)
        val clasificacionTextView: TextView = view.findViewById(R.id.clasificacionTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)

        idTextView.text = "ID: $id"
        nombreTextView.text = "Nombre: $nombre"
        fechaCreacionTextView.text = "Fecha de Creación: $fechaCreacion"
        totalPalomasTextView.text = "Total de Palomas: $totalPalomas"
        clasificacionTextView.text = "Clasificación: $clasificacion"
        statusTextView.text = "Status: $status"

        return view
    }
}
