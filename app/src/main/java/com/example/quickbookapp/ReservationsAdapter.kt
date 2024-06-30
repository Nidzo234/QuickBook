// ReservationsAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quickbookapp.Booking
import com.example.quickbookapp.R

class ReservationsAdapter(private var reservations: List<Booking>) : RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation)
    }
    fun updateData(newReservations: List<Booking>) {
        reservations = newReservations
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val placeNameTextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        fun bind(reservation: Booking) {
            placeNameTextView.text = reservation.placeName
            dateTextView.text = reservation.date
            timeTextView.text = reservation.timeFrom
        }
    }
}
