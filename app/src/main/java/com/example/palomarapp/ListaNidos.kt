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

class ListaNidos : AppCompatActivity() {
    private lateinit var nidosContainer: LinearLayout
    private lateinit var noDataTextView: TextView

    private val getNidosUrl = "http://192.168.100.2/palomar_api/nidos.php?action=ver_lista"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_nidos)

        nidosContainer = findViewById(R.id.nidosContainer)
        noDataTextView = findViewById(R.id.noDataTextView)

        fetchNidos()
    }

    private fun fetchNidos() {
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, getNidosUrl, null,
            Response.Listener { response ->
                try {
                    val nidosArray = response.getJSONArray("data")
                    if (nidosArray.length() > 0) {
                        noDataTextView.visibility = View.GONE
                        nidosContainer.visibility = View.VISIBLE

                        for (i in 0 until nidosArray.length()) {
                            val nido = nidosArray.getJSONObject(i)

                            val numeroNido = nido.getString("numero_nido")
                            val palomaMadreId = nido.getString("paloma_madre_id")
                            val palomaPadreId = nido.getString("paloma_padre_id")
                            val fechaCreacionNido = nido.getString("fecha_creacion_nido")
                            val anillosPichones = nido.getString("anillos_pichones")
                            val cantidadPichonesLogrados = nido.getString("cantidad_pichones_logrados")
                            val status = nido.getString("status")

                            val nidoView = createNidoView(
                                numeroNido,
                                palomaMadreId,
                                palomaPadreId,
                                fechaCreacionNido,
                                anillosPichones,
                                cantidadPichonesLogrados,
                                status
                            )

                            nidosContainer.addView(nidoView)
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
                noDataTextView.text = "Error al obtener los nidos."
                noDataTextView.visibility = View.VISIBLE
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun createNidoView(
        numeroNido: String,
        palomaMadreId: String,
        palomaPadreId: String,
        fechaCreacionNido: String,
        anillosPichones: String,
        cantidadPichonesLogrados: String,
        status: String
    ): View {
        val view = layoutInflater.inflate(R.layout.nido_item, null)

        val numeroNidoTextView: TextView = view.findViewById(R.id.numeroNidoTextView)
        val palomaMadreIdTextView: TextView = view.findViewById(R.id.palomaMadreIdTextView)
        val palomaPadreIdTextView: TextView = view.findViewById(R.id.palomaPadreIdTextView)
        val fechaCreacionNidoTextView: TextView = view.findViewById(R.id.fechaCreacionNidoTextView)
        val anillosPichonesTextView: TextView = view.findViewById(R.id.anillosPichonesTextView)
        val cantidadPichonesLogradosTextView: TextView = view.findViewById(R.id.cantidadPichonesLogradosTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)

        numeroNidoTextView.text = "Número de Nido: $numeroNido"
        palomaMadreIdTextView.text = "ID Paloma Madre: $palomaMadreId"
        palomaPadreIdTextView.text = "ID Paloma Padre: $palomaPadreId"
        fechaCreacionNidoTextView.text = "Fecha de Creación: $fechaCreacionNido"
        anillosPichonesTextView.text = "Anillos de Pichones: $anillosPichones"
        cantidadPichonesLogradosTextView.text = "Cantidad de Pichones: $cantidadPichonesLogrados"
        statusTextView.text = "Estado: $status"

        return view
    }
}
