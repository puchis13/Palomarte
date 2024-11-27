package com.example.palomarapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompetenciasAdapter(private var competencias: List<Competencia>) :
    RecyclerView.Adapter<CompetenciasAdapter.CompetenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_competencia, parent, false)
        return CompetenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompetenciaViewHolder, position: Int) {
        val competencia = competencias[position]
        holder.nombreClub.text = competencia.nombreClub
        holder.clasificacionVuelo.text = competencia.clasificacionVuelo
        holder.ubicacionSuelta.text = competencia.ubicacionSuelta
        holder.fechaCompetencia.text = competencia.fechaCompetencia
        holder.kilometros.text = competencia.kilometros.toString()
    }

    override fun getItemCount(): Int = competencias.size

    fun updateData(newData: List<Competencia>) {
        competencias = newData
        notifyDataSetChanged()
    }

    class CompetenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreClub: TextView = itemView.findViewById(R.id.competenciaNombre)
        val clasificacionVuelo: TextView = itemView.findViewById(R.id.competenciaClasificacion)
        val ubicacionSuelta: TextView = itemView.findViewById(R.id.competenciaUbicacion)
        val fechaCompetencia: TextView = itemView.findViewById(R.id.competenciaFecha)
        val kilometros: TextView = itemView.findViewById(R.id.competenciaKilometros)
    }
}
