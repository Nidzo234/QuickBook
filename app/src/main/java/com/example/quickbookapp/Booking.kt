package com.example.quickbookapp

data class Booking(
    val ownerId: String = "",
    val placeId: String = "",
    val placeName: String = "",
    val appointmentId: String = "",
    val date: String = "",
    val timeFrom: String = ""
)