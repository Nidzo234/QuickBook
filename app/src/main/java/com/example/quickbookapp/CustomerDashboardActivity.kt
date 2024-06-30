package com.example.quickbookapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class CustomerDashboardActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var placesAdapter: PlacesAdapter
    private var placesList = mutableListOf<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)

        firestore = FirebaseFirestore.getInstance()
        placesRecyclerView = findViewById(R.id.placesRecyclerView)
        placesRecyclerView.layoutManager = LinearLayoutManager(this)
        placesAdapter = PlacesAdapter(this, placesList)
        placesRecyclerView.adapter = placesAdapter

        setupFilterButtons()
        setupSearchBar()
        onViewReservationsButtonClick()
        loadAllPlaces()
    }

    private fun setupFilterButtons() {

        val showAll = findViewById<Button>(R.id.buttonAll)
        showAll.setOnClickListener {
            loadAllPlaces()
        }

        val barberButton = findViewById<Button>(R.id.buttonBarber)
        barberButton.setOnClickListener {
            filterPlacesByType("Barber")
        }

        val hairSalonButton = findViewById<Button>(R.id.buttonHairSalon)
        hairSalonButton.setOnClickListener {
            filterPlacesByType("Hair Salon")
        }

        val beautySalonButton = findViewById<Button>(R.id.buttonBeautySalon)
        beautySalonButton.setOnClickListener {
            filterPlacesByType("Beauty Salon")
        }

        val otherButton = findViewById<Button>(R.id.buttonOther)
        otherButton.setOnClickListener {
            filterPlacesByType("Other")
        }
    }

    fun onViewReservationsButtonClick() {
        val buttonViewReservations = findViewById<Button>(R.id.buttonViewReservations)
        buttonViewReservations.setOnClickListener {
            val intent = Intent(this, ReservationsActivity::class.java)
            startActivity(intent)
        }
    }


    private fun loadAllPlaces() {
        firestore.collection("places")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    placesList.clear()
                    for (document in querySnapshot.documents) {
                        val place = document.toObject<Place>()
                        place?.let {
                            placesList.add(it)
                        }
                    }
                    placesAdapter.updateList(placesList)
                } else {
                    Log.d("CustomerDashboard", "No places found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CustomerDashboard", "Error fetching places", exception)
            }
    }
    private fun setupSearchBar() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener { text ->
            placesAdapter.filter(text.toString())
        }
    }

    private fun filterPlacesByType(type: String) {
        val filteredList = placesList.filter { it.type == type }.toMutableList()
        placesAdapter.updateList(filteredList)
    }
}
