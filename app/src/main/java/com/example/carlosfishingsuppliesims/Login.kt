package com.example.carlosfishingsuppliesims

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.loginEmail)
        val passwordEditText = findViewById<EditText>(R.id.loginPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPassword)
        progressBar = findViewById(R.id.progressBar)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                showProgressBar()
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Click listener for the "com.example.carlosfishingsuppliesims.Forgot password" TextView
        forgotPasswordTextView.setOnClickListener {
            // Handle navigation to ForgotPassword activity
            val intent = Intent(this, Forgot::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // User is signed in and email is verified, navigate to LandingPage
                        hideProgressBar()
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        navigateToLandingPage()
                    } else {
                        // Email is not verified, show a message to the user
                        hideProgressBar()
                        Toast.makeText(this, "Email is not verified. Please check your email for verification link.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Login failed, display an error message to the user
                    hideProgressBar()
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun navigateToLandingPage() {
        val intent = Intent(this, LandingPage::class.java)
        startActivity(intent)
        finish() // Finish this activity after navigating
    }
}
