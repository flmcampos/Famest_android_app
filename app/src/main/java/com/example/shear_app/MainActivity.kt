package com.example.famest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        goToSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        goToData.setOnClickListener {
            startActivity(Intent(this, DataActivity::class.java))
        }

        goToHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
}
