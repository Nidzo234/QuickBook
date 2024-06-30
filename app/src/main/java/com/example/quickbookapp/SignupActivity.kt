package com.example.quickbookapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val userTypeRadioGroup = findViewById<RadioGroup>(R.id.userTypeRadioGroup)
        val signupButton = findViewById<Button>(R.id.signupButton)


        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val selectedUserType = when (userTypeRadioGroup.checkedRadioButtonId) {
                R.id.ownerRadioButton -> "owner"
                R.id.customerRadioButton -> "customer"
                else -> ""
            }

            if (email.isNotEmpty() && password.isNotEmpty() && selectedUserType.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = hashMapOf(
                                "email" to email,
                                "userType" to selectedUserType
                            )
                            firestore.collection("users").document(auth.currentUser!!.uid)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                                    //navigateToDashboard(selectedUserType)

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            finish()
                        } else {
                            Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }}

    }

    private fun navigateToDashboard(userType: String) {
        val intent = when (userType) {
            "owner" -> Intent(this, OwnerDashboardActivity::class.java)
            //"customer" -> Intent(this, CustomerDashboardActivity::class.java)
            else -> null
        }
        intent?.let {
            startActivity(it)
            finish()
        }
    }
}