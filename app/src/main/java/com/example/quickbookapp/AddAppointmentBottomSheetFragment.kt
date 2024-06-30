package com.example.quickbookapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface AddAppointmentDialogListener {
    fun onAppointmentAdded(date: String, timeFrom: String)
}

class AddAppointmentBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var listener: AddAppointmentDialogListener
    private lateinit var datePicker: DatePicker
    private lateinit var timePickerFrom: TimePicker
    private lateinit var timePickerTo: TimePicker
    private lateinit var addButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_add_appointment_bottom_sheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePicker = view.findViewById(R.id.datePicker)
        timePickerFrom = view.findViewById(R.id.timePickerFrom)
        addButton = view.findViewById(R.id.buttonAdd)

        addButton.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val date = String.format("%04d-%02d-%02d", year, month + 1, day) // Adjust month + 1 because DatePicker month is 0-indexed
            val timeFrom = String.format("%02d:%02d", timePickerFrom.hour, timePickerFrom.minute)

            if (date.isNotEmpty() && timeFrom.isNotEmpty()) {
                listener.onAppointmentAdded(date, timeFrom)
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddAppointmentDialogListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement AddAppointmentDialogListener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddAppointmentBottomSheetFragment()
    }
}
