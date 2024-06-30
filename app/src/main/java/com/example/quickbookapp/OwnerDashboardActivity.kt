package com.example.quickbookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class OwnerDashboardActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var placesAdapter: PlacesAdapter
    private val placesList = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_dashboard)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val addPlaceButton = findViewById<Button>(R.id.addPlaceButton)
        addPlaceButton.setOnClickListener {
            startActivity(Intent(this, AddPlaceActivity::class.java))

        }

        placesRecyclerView = findViewById(R.id.placesRecyclerView)
        placesRecyclerView.layoutManager = LinearLayoutManager(this)
        placesAdapter = PlacesAdapter(this, placesList)
        placesRecyclerView.adapter = placesAdapter

        loadPlaces()
    }
    override fun onResume() {
        super.onResume()
        loadPlaces()
    }

    private fun loadPlaces() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("places")
                .whereEqualTo("ownerId", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    placesList.clear()
                    for (document in result) {
                        val place = document.toObject(Place::class.java)
                        placesList.add(place)
                    }
                    placesAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting places: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}