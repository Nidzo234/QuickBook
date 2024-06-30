package com.example.quickbookapp

import ReservationsAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReservationsActivity : AppCompatActivity() {
    private lateinit var reservationsRecyclerView: RecyclerView
    private lateinit var reservationsAdapter: ReservationsAdapter

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var backButton: Button

    private val placesList = mutableListOf<Booking>()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reservations)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            fetchUserReservations(currentUser.uid)
        }


        reservationsRecyclerView = findViewById(R.id.reservationsRecyclerView)
        reservationsAdapter = ReservationsAdapter(emptyList())
        reservationsRecyclerView.adapter = reservationsAdapter
        reservationsRecyclerView.layoutManager = LinearLayoutManager(this)


        backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, CustomerDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun fetchUserReservations(userId: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("bookings")
                .whereEqualTo("ownerId", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    placesList.clear()
                    for (document in result) {
                        val place = document.toObject(Booking::class.java)
                        placesList.add(place)
                    }
                    reservationsAdapter.updateData(placesList)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting places: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}