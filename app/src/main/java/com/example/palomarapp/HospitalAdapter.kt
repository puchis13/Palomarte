import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.palomarapp.R

class HospitalAdapter(private var hospitals: ArrayList<Hospital>) : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    // Actualiza los datos en el adaptador
    fun updateData(newHospitals: ArrayList<Hospital>) {
        hospitals.clear()
        hospitals.addAll(newHospitals)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_hospital, parent, false)
        return HospitalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitals[position]
        holder.bind(hospital)
    }

    override fun getItemCount(): Int {
        return hospitals.size
    }

    // ViewHolder para cada item
    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFechaSintomas: TextView = itemView.findViewById(R.id.tvFechaSintomas)
        private val tvPalomaId: TextView = itemView.findViewById(R.id.tvPalomaId)
        private val tvDescripcionEnfermedad: TextView = itemView.findViewById(R.id.tvDescripcionEnfermedad)
        private val tvMedicacion: TextView = itemView.findViewById(R.id.tvMedicacion)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(hospital: Hospital) {
            tvFechaSintomas.text = hospital.fechaSintomas
            tvPalomaId.text = hospital.palomaId
            tvDescripcionEnfermedad.text = hospital.descripcionEnfermedad
            tvMedicacion.text = hospital.medicacion
            tvStatus.text = hospital.status
        }
    }
}

