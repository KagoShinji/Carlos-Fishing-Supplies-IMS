package com.example.carlosfishingsuppliesims

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add a delay before directing to MainActivity (optional)
        val delayMillis = 3000 // 3 seconds delay (adjust as needed)
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        // Post a delayed action to start MainActivity after delayMillis
        findViewById<View>(R.id.main).postDelayed({
            startActivity(mainActivityIntent)
            finish() // Finish this activity to prevent going back to Splash using back button
        }, delayMillis.toLong())
    }
}
