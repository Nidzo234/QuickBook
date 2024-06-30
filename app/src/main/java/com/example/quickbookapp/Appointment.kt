package com.example.quickbookapp

data class Appointment(
    val appointmentId: String = "",
    val date: String = "",
    val timeFrom: String = "",
    var isAvailable: Boolean = true
)
