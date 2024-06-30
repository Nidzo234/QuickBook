package com.example.quickbookapp

import AppointmentAdapter
import AppointmentClickListener
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class PlaceDetailActivity : AppCompatActivity(), OnMapReadyCallback, AddAppointmentDialogListener, AppointmentClickListener {

    private lateinit var mMap: GoogleMap
    private var placeLatitude: Double = 0.0
    private var placeLongitude: Double = 0.0
    private lateinit var makeReservationButton: Button
    private lateinit var addTerminButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var placeId: String
    private lateinit var placeName: String
    private var selectedAppointment: Appointment? = null

    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentAdapter: AppointmentAdapter




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        firestore = FirebaseFirestore.getInstance()

        placeId = intent.getStringExtra("PLACE_ID") ?: ""
        Toast.makeText(this, placeId.toString(), Toast.LENGTH_SHORT).show()
        placeName = intent.getStringExtra("PLACE_NAME").toString()
        placeLatitude = intent.getDoubleExtra("PLACE_LATITUDE", 0.0)
        placeLongitude = intent.getDoubleExtra("PLACE_LONGITUDE", 0.0)
        val placeOwnerId = intent.getStringExtra("PLACE_OWNER_ID")
        val placeDescription = intent.getStringExtra("PLACE_DESCRIPTION")
        val placeType = intent.getStringExtra("PLACE_TYPE")

        val placeNameTextView = findViewById<TextView>(R.id.placeNameTextView)

        val placeDescriptionTextView = findViewById<TextView>(R.id.placeDescriptionTextView)
        val placeTypeTextView = findViewById<TextView>(R.id.placeTypeTextView)
        addTerminButton = findViewById<Button>(R.id.addTerminButton)



        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        appointmentAdapter = AppointmentAdapter(listOf(), this)
        appointmentsRecyclerView.adapter = appointmentAdapter



        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && currentUser.uid == placeOwnerId) {
            addTerminButton.visibility = View.VISIBLE
            addTerminButton.setOnClickListener {
                val bottomSheet = AddAppointmentBottomSheetFragment.newInstance()
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }

        placeNameTextView.text = placeName

        placeDescriptionTextView.text = "Description: $placeDescription"
        placeTypeTextView.text = "Type: $placeType"



        showAppointments()




        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val placeLocation = LatLng(placeLatitude, placeLongitude)
        mMap.addMarker(MarkerOptions().position(placeLocation).title("Place Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))
    }

    override fun onAppointmentAdded(date: String, timeFrom: String) {
        val appointmentId = UUID.randomUUID().toString()
        val appointment = Appointment(appointmentId, date, timeFrom, true)


        val placeRef = firestore.collection("places").document(placeId)

        placeRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                Toast.makeText(this, "najden zapis", Toast.LENGTH_SHORT).show()
                val appointments = documentSnapshot.get("appointments")
                if (appointments != null) {

                    placeRef.update("appointments", FieldValue.arrayUnion(appointment))
                        .addOnSuccessListener {
                            Log.d("PlaceDetailActivity", "Appointment added successfully")
                            showAppointments()
                        }
                        .addOnFailureListener { e ->
                            Log.w("PlaceDetailActivity", "Error adding appointment", e)
                        }
                } else {
                    placeRef.update("appointments", listOf(appointment))
                        .addOnSuccessListener {
                            Log.d("PlaceDetailActivity", "Appointments array initialized and appointment added successfully")
                            showAppointments()
                        }
                        .addOnFailureListener { e ->
                            Log.w("PlaceDetailActivity", "Error initializing appointments array", e)
                        }
                }
            }
        }.addOnFailureListener { e ->
            Log.w("PlaceDetailActivity", "Error checking if document exists", e)
        }
    }


    private fun showAppointments() {
        val placeRef = firestore.collection("places").document(placeId)

        placeRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val appointments = documentSnapshot.get("appointments") as? List<Map<String, Any>>
                if (appointments != null) {
                    val appointmentList = appointments.map { appointmentMap ->
                        Appointment(
                            appointmentId = appointmentMap["appointmentId"] as String,
                            date = appointmentMap["date"] as String,
                            timeFrom = appointmentMap["timeFrom"] as String,
                            isAvailable = appointmentMap["free"] as? Boolean ?: true
                        )
                    }
                    appointmentAdapter.updateAppointments(appointmentList)
                    appointmentsRecyclerView.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            Log.w("PlaceDetailActivity", "Error fetching appointments", e)
        }
    }


    override fun onReservationClick(appointment: Appointment) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val ownerId = currentUser?.uid ?: ""
        val placeId = placeId
        val appointmentId = appointment.appointmentId
        val date = appointment.date
        val timeFrom = appointment.timeFrom
        val name = placeName

        val booking = Booking(ownerId, placeId,name, appointmentId, date, timeFrom)


        val bookingsRef = firestore.collection("bookings")


        bookingsRef.add(booking)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Booking added with ID: ${documentReference.id}")

                val placeRef = firestore.collection("places").document(placeId).collection("appointments").document(appointmentId)

                placeRef.update("free", false)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Appointment reserved successfully", Toast.LENGTH_SHORT).show()
                        appointment.isAvailable = false
                        appointmentAdapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PlaceDetailActivity", "Error reserving appointment", e)

                    }
                Toast.makeText(this, "Uspesno rezerviran termin", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ReservationsActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("PlaceDetailActivity", "Error adding booking", e)
                Toast.makeText(this, "Failed to add booking", Toast.LENGTH_SHORT).show()
            }
    }







}
