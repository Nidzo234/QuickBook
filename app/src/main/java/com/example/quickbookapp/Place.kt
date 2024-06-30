package com.example.quickbookapp

data class Place(
    val placeId: String = "",
    val name: String = "",
    val type: String = "", // "restaurant", "apartment", "hotel", "salon"
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: String = "",
    val appointments: List<Appointment> = listOf()
)