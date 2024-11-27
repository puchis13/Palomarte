package com.example.palomarapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class ViewPalomasActivity : AppCompatActivity() {

    private lateinit var palomasContainer: LinearLayout
    private lateinit var noDataTextView: TextView

    // URL para obtener las palomas
    private val getPalomasUrl = "http://192.168.100.2/palomar_api/get_palomas.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_palomas)

        palomasContainer = findViewById(R.id.palomasContainer)
        noDataTextView = findViewById(R.id.noDataTextView)

        fetchPalomas()
    }

    private fun fetchPalomas() {
        val queue = Volley.newRequestQueue(this)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, getPalomasUrl, null,
            Response.Listener { response ->
                if (response.length() > 0) {
                    noDataTextView.visibility = View.GONE
                    palomasContainer.visibility = View.VISIBLE

                    for (i in 0 until response.length()) {
                        val paloma = response.getJSONObject(i)

                        val alias = paloma.getString("alias")
                        val fechaNacimiento = paloma.getString("fecha_nacimiento")
                        val clasificacionAnillo = paloma.getString("clasificacion_anillo")
                        val sexo = paloma.getString("sexo")
                        val colorPlumaje = paloma.getString("color_plumaje")
                        val madreId = paloma.optString("madre_id", "N/A")
                        val padreId = paloma.optString("padre_id", "N/A")
                        val raza = paloma.getString("raza")
                        val capacidadVuelo = paloma.getString("capacidad_vuelo")
                        val status = paloma.getString("status")
                        val fotografia = paloma.optString("fotografia", "")

                        val palomaView = createPalomaView(
                            alias,
                            fechaNacimiento,
                            clasificacionAnillo,
                            sexo,
                            colorPlumaje,
                            madreId,
                            padreId,
                            raza,
                            capacidadVuelo,
                            status,
                            fotografia
                        )

                        palomasContainer.addView(palomaView)
                    }
                } else {
                    noDataTextView.visibility = View.VISIBLE
                }
            },
            Response.ErrorListener { error ->
                Log.e("FetchPalomasError", error.toString())
                noDataTextView.text = "Error al obtener las palomas."
                noDataTextView.visibility = View.VISIBLE
            }
        )

        queue.add(jsonArrayRequest)
    }

    private fun createPalomaView(
        alias: String,
        fechaNacimiento: String,
        clasificacionAnillo: String,
        sexo: String,
        colorPlumaje: String,
        madreId: String,
        padreId: String,
        raza: String,
        capacidadVuelo: String,
        status: String,
        fotografia: String
    ): View {
        val view = layoutInflater.inflate(R.layout.paloma_item, null)

        val aliasTextView: TextView = view.findViewById(R.id.aliasTextView)
        val fechaNacimientoTextView: TextView = view.findViewById(R.id.fechaNacimientoTextView)
        val clasificacionAnilloTextView: TextView = view.findViewById(R.id.clasificacionAnilloTextView)
        val sexoTextView: TextView = view.findViewById(R.id.sexoTextView)
        val colorPlumajeTextView: TextView = view.findViewById(R.id.colorPlumajeTextView)
        val madreIdTextView: TextView = view.findViewById(R.id.madreIdTextView)
        val padreIdTextView: TextView = view.findViewById(R.id.padreIdTextView)
        val razaTextView: TextView = view.findViewById(R.id.razaTextView)
        val capacidadVueloTextView: TextView = view.findViewById(R.id.capacidadVueloTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        val fotografiaImageView: ImageView = view.findViewById(R.id.fotografiaImageView)

        aliasTextView.text = "Alias: $alias"
        fechaNacimientoTextView.text = "Fecha Nacimiento: $fechaNacimiento"
        clasificacionAnilloTextView.text = "Clasificación Anillo: $clasificacionAnillo"
        sexoTextView.text = "Sexo: $sexo"
        colorPlumajeTextView.text = "Color Plumaje: $colorPlumaje"
        madreIdTextView.text = "Madre ID: $madreId"
        padreIdTextView.text = "Padre ID: $padreId"
        razaTextView.text = "Raza: $raza"
        capacidadVueloTextView.text = "Capacidad Vuelo: $capacidadVuelo"
        statusTextView.text = "Status: $status"

        // Procesar y mostrar la fotografía desde la ruta almacenada
        if (fotografia.isNotEmpty()) {
            val imageUrl = "http://192.168.100.2/palomar_api/$fotografia"
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.loading) // Imagen de carga
                .error(R.drawable.palomo) // Imagen de error
                .into(fotografiaImageView)
        } else {
            fotografiaImageView.setImageResource(R.drawable.palomo) // Imagen por defecto
        }

        return view
    }
}
