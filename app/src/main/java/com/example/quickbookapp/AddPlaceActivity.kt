package com.example.quickbookapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


class AddPlaceActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedLocation: LatLng? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val placeTypeSpinner = findViewById<Spinner>(R.id.placeTypeSpinner)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        ArrayAdapter.createFromResource(
            this,
            R.array.place_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            placeTypeSpinner.adapter = adapter
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            savePlace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
        }

        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun savePlace() {
        val placeName = findViewById<EditText>(R.id.placeNameEditText).text.toString()
        val currentUser = auth.currentUser
        val type = findViewById<Spinner>(R.id.placeTypeSpinner).selectedItem.toString()
        val description = findViewById<EditText>(R.id.placeDescriptionEditText).text.toString()


        if (placeName.isNotEmpty() && selectedLocation != null && currentUser != null) {
            val placeId = UUID.randomUUID().toString()
            val place = hashMapOf(
                "name" to placeName,
                "placeId" to placeId,
                "ownerId" to currentUser.uid,
                "latitude" to selectedLocation!!.latitude,
                "longitude" to selectedLocation!!.longitude,
                "description" to description,
                "type" to type,
                "appointments" to listOf<Appointment>()
            )
            firestore.collection("places")
                .document(placeId)
                .set(place)
                .addOnSuccessListener {
                    Toast.makeText(this, "Place added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding place: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please fill in all fields and select a location", Toast.LENGTH_SHORT).show()
        }
    }
}
