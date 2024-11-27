package com.example.palomarapp

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ListaConsumiblesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_consumibles)

        val container = findViewById<LinearLayout>(R.id.consumiblesContainer)
        val noDataTextView = findViewById<TextView>(R.id.noDataTextView)

        fetchConsumibles { consumibles ->
            if (consumibles.isEmpty()) {
                noDataTextView.visibility = TextView.VISIBLE
            } else {
                noDataTextView.visibility = TextView.GONE
                for (consumible in consumibles) {
                    val textView = TextView(this)
                    textView.text = consumible.nombre
                    container.addView(textView)
                }
            }
        }
    }

    private fun fetchConsumibles(callback: (List<Consumible>) -> Unit) {
        val url = URL("http://192.168.100.2/palomar_api/consumibles")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        val inputStream = InputStreamReader(urlConnection.inputStream)
        val jsonResponse = JSONArray(inputStream.readText())
        val consumibles = parseConsumibles(jsonResponse)
        callback(consumibles)
    }

    private fun parseConsumibles(json: JSONArray): List<Consumible> {
        val consumibles = mutableListOf<Consumible>()
        for (i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val consumible = Consumible(
                item.getInt("id"),
                item.getString("nombre"),
                item.getString("descripcion"),
                item.getInt("cantidad"),
                item.getString("tipo"),
                item.getString("fotografia")
            )
            consumibles.add(consumible)
        }
        return consumibles
    }
}
