package com.example.quickbookapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class AddAppointmentDialog : DialogFragment() {

    private lateinit var listener: AddAppointmentDialogListener

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.activity_add_appointment_dialog, null)

            val editTextDate = view.findViewById<EditText>(R.id.editTextDate)
            val editTextTimeFrom = view.findViewById<EditText>(R.id.editTextTimeFrom)
            val editTextTimeTo = view.findViewById<EditText>(R.id.editTextTimeTo)
            val buttonAdd = view.findViewById<Button>(R.id.buttonAdd)

            buttonAdd.setOnClickListener {
                val date = editTextDate.text.toString()
                val timeFrom = editTextTimeFrom.text.toString()
                val timeTo = editTextTimeTo.text.toString()

                if (date.isNotEmpty() && timeFrom.isNotEmpty() && timeTo.isNotEmpty()) {
                    listener.onAppointmentAdded(date, timeFrom, timeTo)
                    dismiss()
                }
            }

            builder.setView(view)
                .setTitle("Add Appointment")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AddAppointmentDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement AddAppointmentDialogListener"))
        }
    }

    interface AddAppointmentDialogListener {
        fun onAppointmentAdded(date: String, timeFrom: String, timeTo: String)
    }
}
