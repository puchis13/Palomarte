package com.example.palomarapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MenuGridAdapter(
    private val context: Context,
    private val menuItems: List<MenuItem>
) : BaseAdapter() {

    override fun getCount(): Int {
        return menuItems.size
    }

    override fun getItem(position: Int): Any {
        return menuItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_menu_grid, parent, false)
        val icon: ImageView = view.findViewById(R.id.menuIcon)
        val text: TextView = view.findViewById(R.id.menuText)

        val menuItem = menuItems[position]
        icon.setImageResource(menuItem.iconResId)
        text.text = menuItem.title

        // Configura el evento de clic para redirigir a la actividad correspondiente
        view.setOnClickListener {
            context.startActivity(Intent(context, menuItem.activityClass))
        }

        return view
    }
}

data class MenuItem(
    val title: String,
    val iconResId: Int,
    val activityClass: Class<*>
)
