package com.example.quickbookapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class PlacesAdapter(private val context: Context, private var places: List<Place>) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private var placesFullList: List<Place> = places

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeNameTextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        val placeDescriptionTextView: TextView = itemView.findViewById(R.id.placeDescriptionTextView)
        val placeTyoeTextView: TextView = itemView.findViewById(R.id.placeTypeTextView)
        val seeMoreButton: Button = itemView.findViewById(R.id.seeMoreButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.placeNameTextView.text = place.name
        holder.placeDescriptionTextView.text = place.description
        holder.placeTyoeTextView.text = place.type
        holder.seeMoreButton.setOnClickListener {
            val intent = Intent(context, PlaceDetailActivity::class.java).apply {
                putExtra("PLACE_ID", place.placeId)
                putExtra("PLACE_NAME", place.name)
                putExtra("PLACE_LATITUDE", place.latitude)
                putExtra("PLACE_LONGITUDE", place.longitude)
                putExtra("PLACE_OWNER_ID", place.ownerId)
                putExtra("PLACE_DESCRIPTION", place.description)
                putExtra("PLACE_TYPE", place.type)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = places.size

    fun updateList(newList: MutableList<Place>) {
        places = newList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        if (!query.isEmpty()) {
            places = placesFullList.filter { it.name.contains(query, true) }
        } else {
            places = placesFullList
        }
        notifyDataSetChanged()
    }

}
