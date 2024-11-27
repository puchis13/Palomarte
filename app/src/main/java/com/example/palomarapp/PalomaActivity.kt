package com.example.palomarapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.File

class PalomaActivity : AppCompatActivity() {

    // Declarar los campos de entrada
    private lateinit var idInput: EditText
    private lateinit var aliasInput: EditText
    private lateinit var fechaNacimientoInput: EditText
    private lateinit var clasificacionAnilloSpinner: Spinner
    private lateinit var sexoGroup: RadioGroup
    private lateinit var colorPlumajeSpinner: Spinner
    private lateinit var madreIdInput: EditText
    private lateinit var padreIdInput: EditText
    private lateinit var razaInput: EditText
    private lateinit var capacidadVueloSpinner: Spinner
    private lateinit var statusGroup: RadioGroup
    private lateinit var createButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var takePhotoButton: Button
    private lateinit var selectPhotoButton: Button
    private lateinit var imagePreview: ImageView
    private lateinit var viewPalomasButton: Button

    private var photoUri: Uri? = null
    private lateinit var currentPhotoPath: String
    private var photoBase64: String? = null

    // URLs del servidor
    private val insertUrl = "http://192.168.100.2/palomar_api/insert_paloma.php"
    private val updateUrl = "http://192.168.100.2/palomar_api/update_paloma.php"
    private val deleteUrl = "http://192.168.100.2/palomar_api/delete_paloma.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paloma)

        // Inicializar los campos de entrada
        idInput = findViewById(R.id.idInput)
        aliasInput = findViewById(R.id.aliasInput)
        fechaNacimientoInput = findViewById(R.id.fechaNacimientoInput)
        clasificacionAnilloSpinner = findViewById(R.id.clasificacionAnilloSpinner)
        sexoGroup = findViewById(R.id.sexoGroup)
        colorPlumajeSpinner = findViewById(R.id.colorPlumajeSpinner)
        madreIdInput = findViewById(R.id.madreIdInput)
        padreIdInput = findViewById(R.id.padreIdInput)
        razaInput = findViewById(R.id.razaInput)
        capacidadVueloSpinner = findViewById(R.id.capacidadVueloSpinner)
        statusGroup = findViewById(R.id.statusGroup)
        createButton = findViewById(R.id.createButton)
        updateButton = findViewById(R.id.updateButton)
        deleteButton = findViewById(R.id.deleteButton)
        takePhotoButton = findViewById(R.id.takePhotoButton)
        selectPhotoButton = findViewById(R.id.selectPhotoButton)
        imagePreview = findViewById(R.id.imagePreview)
        viewPalomasButton = findViewById(R.id.viewPalomasButton)

        viewPalomasButton.setOnClickListener {
            val intent = Intent(this, ViewPalomasActivity::class.java)
            startActivity(intent)
        }


        // Configurar Spinners
        setupSpinners()

        // Configurar botones
        createButton.setOnClickListener { insertPaloma() }
        updateButton.setOnClickListener { updatePaloma() }
        deleteButton.setOnClickListener { deletePaloma() }
        takePhotoButton.setOnClickListener { takePhoto() }
        selectPhotoButton.setOnClickListener { selectPhotoFromGallery() }
    }

    private fun setupSpinners() {
        val clasificacionAnilloOptions = arrayOf("Ordinario", "Derby", "Dropper")
        clasificacionAnilloSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clasificacionAnilloOptions)

        val colorPlumajeOptions = arrayOf("Azul", "Negro", "Rojo", "Blanco")
        colorPlumajeSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, colorPlumajeOptions)

        val capacidadVueloOptions = arrayOf("Fondo", "Medio Fondo", "Velocidad")
        capacidadVueloSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, capacidadVueloOptions)
    }

    private fun getSelectedSexo(): String {
        return when (sexoGroup.checkedRadioButtonId) {
            R.id.sexoMacho -> "Macho"
            R.id.sexoHembra -> "Hembra"
            else -> ""
        }
    }

    private fun getSelectedStatus(): String {
        return when (statusGroup.checkedRadioButtonId) {
            R.id.statusActivo -> "Activo"
            R.id.statusInactivo -> "Inactivo"
            else -> ""
        }
    }
    private fun insertPaloma() {
        val id = idInput.text.toString().trim()
        val alias = aliasInput.text.toString().trim()
        val fechaNacimiento = fechaNacimientoInput.text.toString().trim()
        val clasificacionAnillo = clasificacionAnilloSpinner.selectedItem?.toString()?.trim() ?: ""
        val sexo = getSelectedSexo()
        val colorPlumaje = colorPlumajeSpinner.selectedItem?.toString()?.trim() ?: ""
        val madreId = madreIdInput.text.toString().trim()
        val padreId = padreIdInput.text.toString().trim()
        val raza = razaInput.text.toString().trim()
        val capacidadVuelo = capacidadVueloSpinner.selectedItem?.toString()?.trim() ?: ""
        val status = getSelectedStatus()

        if (id.isEmpty() || alias.isEmpty() || fechaNacimiento.isEmpty() || sexo.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>().apply {
            put("id", id)
            put("alias", alias)
            put("fecha_nacimiento", fechaNacimiento)
            put("clasificacion_anillo", clasificacionAnillo)
            put("sexo", sexo)
            put("color_plumaje", colorPlumaje)
            put("madre_id", madreId)
            put("padre_id", padreId)
            put("raza", raza)
            put("capacidad_vuelo", capacidadVuelo)
            put("status", status)
            if (!photoBase64.isNullOrEmpty()) put("fotografia", photoBase64!!)
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, insertUrl,
            Response.Listener { response ->
                Log.d("InsertResponse", response)
                Toast.makeText(this, "Paloma creada correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
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
        val id = idInput.text.toString().trim()

        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el ID para actualizar.", Toast.LENGTH_SHORT).show()
            return
        }

        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>().apply {
            put("id", id)
            put("alias", aliasInput.text.toString())
            put("fecha_nacimiento", fechaNacimientoInput.text.toString())
            put("clasificacion_anillo", clasificacionAnilloSpinner.selectedItem.toString())
            put("sexo", getSelectedSexo())
            put("color_plumaje", colorPlumajeSpinner.selectedItem.toString())
            put("madre_id", madreIdInput.text.toString())
            put("padre_id", padreIdInput.text.toString())
            put("raza", razaInput.text.toString())
            put("capacidad_vuelo", capacidadVueloSpinner.selectedItem.toString())
            put("status", getSelectedStatus())
            if (!photoBase64.isNullOrEmpty()) put("fotografia", photoBase64!!)
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, updateUrl,
            Response.Listener { response ->
                Log.d("UpdateResponse", response)
                Toast.makeText(this, "Paloma actualizada correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
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
        val id = idInput.text.toString().trim()

        if (id.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el ID para eliminar.", Toast.LENGTH_SHORT).show()
            return
        }

        val queue = Volley.newRequestQueue(this)

        val params = HashMap<String, String>().apply {
            put("id", id)
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, deleteUrl,
            Response.Listener { response ->
                Log.d("DeleteResponse", response)
                Toast.makeText(this, "Paloma eliminada correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
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

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile = createImageFile()
            photoFile?.let {
                photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                resultLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis().toString()
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_$timeStamp", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun selectPhotoFromGallery() {
        val selectPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(selectPhotoIntent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            photoUri = data?.data ?: photoUri
            imagePreview.setImageURI(photoUri)
            photoUri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                photoBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun clearFields() {
        idInput.text.clear()
        aliasInput.text.clear()
        fechaNacimientoInput.text.clear()
        madreIdInput.text.clear()
        padreIdInput.text.clear()
        razaInput.text.clear()
        clasificacionAnilloSpinner.setSelection(0)
        colorPlumajeSpinner.setSelection(0)
        capacidadVueloSpinner.setSelection(0)
        sexoGroup.clearCheck()
        statusGroup.clearCheck()
        imagePreview.setImageResource(0)
        photoUri = null
        photoBase64 = null
    }
}
