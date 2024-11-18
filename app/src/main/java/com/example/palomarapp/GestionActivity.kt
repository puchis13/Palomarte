package com.example.palomarapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.GridView

class GestionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion)

        val gridView: GridView = findViewById(R.id.menuGridView)

        // Lista de opciones del menú
        val menuItems = listOf(
            MenuItem("Paloma", R.drawable.ic_pigeon, PalomaActivity::class.java),
            MenuItem("Reproducción", R.drawable.ic_reproduction, ReproduccionActivity::class.java),
            MenuItem("Grupos", R.drawable.ic_groups, GruposActivity::class.java),
            MenuItem("Entrenamientos", R.drawable.ic_training, EntrenamientosActivity::class.java),
            MenuItem("Competencias", R.drawable.ic_competitions, CompetenciasActivity::class.java),
            MenuItem("Expediente Médico", R.drawable.ic_medical_record, ExpedienteMedicoActivity::class.java),
            MenuItem("Inventario", R.drawable.ic_inventory, InventarioActivity::class.java)
        )

        // Configurar el adaptador
        val adapter = MenuGridAdapter(this, menuItems)
        gridView.adapter = adapter
    }
}
