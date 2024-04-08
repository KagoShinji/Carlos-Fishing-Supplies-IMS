package com.example.carlosfishingsuppliesims

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Forgot : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.forgotPasswordEmail)
        resetButton = findViewById(R.id.btnConfirm)

        // Set click listener for the resetButton
        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                showLoading()
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Sending email...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun dismissLoading() {
        // Dismiss the progress dialog
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dismissLoading()
                    Toast.makeText(this, "Password reset email sent successfully. Please check your email.", Toast.LENGTH_SHORT).show()
                    // Optional: Navigate back to login screen after sending the reset email
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish() // Optional: finish the current activity
                } else {
                    dismissLoading()
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}


