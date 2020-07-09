package com.example.shear_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.profile_list.*

class ProfileActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_list)

        goToAddProfile.setOnClickListener {
            startActivity(Intent(this, AddProfileActivity::class.java))
        }

        val preferences = getSharedPreferences("database", Context.MODE_PRIVATE)
        val savedName = preferences.getString("savedName", "This value doesn't exist.")

    }
}