package com.example.palomarapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class InventarioActivity : AppCompatActivity() {

    private lateinit var photoImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        val idInput = findViewById<EditText>(R.id.editTextIdConsumible)
        val nombreInput = findViewById<EditText>(R.id.editTextNombreConsumible)
        val descripcionInput = findViewById<EditText>(R.id.editTextDescripcion)
        val cantidadInput = findViewById<EditText>(R.id.editTextCantidadConsumible)
        val tipoInput = findViewById<EditText>(R.id.editTextTipoConsumible)
        photoImageView = findViewById(R.id.foto)

        val createButton = findViewById<Button>(R.id.createButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        val viewListButton = findViewById<Button>(R.id.viewListButton)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        // Tomar foto con la cámara
        selectImageButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        // Buscar Consumible por ID
        searchButton.setOnClickListener {
            val idConsumible = idInput.text.toString()
            if (idConsumible.isNotEmpty()) {
                searchConsumibleById(idConsumible) { consumible ->
                    if (consumible != null) {
                        nombreInput.setText(consumible.nombre)
                        descripcionInput.setText(consumible.descripcion)
                        cantidadInput.setText(consumible.cantidad.toString())
                        tipoInput.setText(consumible.tipo)
                        // Mostrar la imagen en el ImageView
                        val decodedBytes = Base64.decode(consumible.fotografia, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        photoImageView.setImageBitmap(bitmap)
                    } else {
                        Toast.makeText(this, "Consumible no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Crear Consumible
        createButton.setOnClickListener {
            val params = mapOf(
                "nombre" to nombreInput.text.toString(),
                "descripcion" to descripcionInput.text.toString(),
                "cantidad" to cantidadInput.text.toString(),
                "tipo" to tipoInput.text.toString(),
                "fotografia" to convertImageToBase64()
            )
            sendHttpRequest("http://192.168.100.2/palomar_api/consumibles", params, "POST")
        }

        // Actualizar Consumible
        updateButton.setOnClickListener {
            val params = mapOf(
                "id" to idInput.text.toString(),
                "nombre" to nombreInput.text.toString(),
                "descripcion" to descripcionInput.text.toString(),
                "cantidad" to cantidadInput.text.toString(),
                "tipo" to tipoInput.text.toString(),
                "fotografia" to convertImageToBase64()
            )
            sendHttpRequest("http://192.168.100.2/palomar_api/consumibles", params, "PUT")
        }

        // Eliminar Consumible
        deleteButton.setOnClickListener {
            val idConsumible = idInput.text.toString()
            if (idConsumible.isNotEmpty()) {
                deleteConsumible(idConsumible)
            } else {
                Toast.makeText(this, "Por favor ingrese un ID", Toast.LENGTH_SHORT).show()
            }
        }

        // Ver lista de Consumibles
        viewListButton.setOnClickListener {
            val intent = Intent(this, ListaConsumiblesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun convertImageToBase64(): String {
        val bitmap = (photoImageView.drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun sendHttpRequest(urlString: String, params: Map<String, String>, method: String) {
        // Este método enviaría las peticiones HTTP al servidor para crear, actualizar o eliminar los consumibles.
        // Asegúrate de implementar la lógica para manejar la petición y las respuestas correctamente.
    }

    private fun searchConsumibleById(idConsumible: String, callback: (Consumible?) -> Unit) {
        val url = URL("http://192.168.100.2/palomar_api/consumibles/$idConsumible")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        val inputStream = InputStreamReader(urlConnection.inputStream)
        val jsonResponse = JSONArray(inputStream.readText())
        val consumible = parseConsumible(jsonResponse)
        callback(consumible)
    }

    private fun parseConsumible(json: JSONArray): Consumible? {
        // Implementa aquí el parseo del JSON y devuelve el objeto consumible
        return null
    }

    private fun deleteConsumible(idConsumible: String) {
        // Implementa aquí la lógica para eliminar el consumible desde el servidor.
    }
}
