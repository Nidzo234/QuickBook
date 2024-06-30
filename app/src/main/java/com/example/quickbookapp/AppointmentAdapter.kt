// AppointmentAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quickbookapp.Appointment
import com.example.quickbookapp.R


interface AppointmentClickListener {
    fun onReservationClick(appointment: Appointment)
}

class AppointmentAdapter(private var appointments: List<Appointment>, private val listener: AppointmentClickListener) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {



    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.appointmentDateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.appointmentTimeTextView)
        val reservationButton: Button = itemView.findViewById(R.id.makeReservationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.dateTextView.text = appointment.date
        holder.timeTextView.text = appointment.timeFrom

        holder.reservationButton.setOnClickListener {
            listener.onReservationClick(appointment)
        }
    }

    override fun getItemCount() = appointments.size

    fun updateAppointments(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}
