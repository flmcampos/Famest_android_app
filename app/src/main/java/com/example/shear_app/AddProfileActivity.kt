package com.example.shear_app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.profile.*
import kotlinx.android.synthetic.main.profile_list.*

class AddProfileActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        buttonSave.setOnClickListener {

            val database = getSharedPreferences("database", Context.MODE_PRIVATE)
            database.edit().apply {
                putString("savedName", editTextNome.text.toString())
                putString("savedAge", editTextIdade.text.toString())
                putString("SavedHeight", editTextAltura.text.toString())
                putString("SavedWeight", editTextPeso.text.toString())
                putString("SavedShoeNumber", editTextNumeroSapato.text.toString())
                }.apply()

            finish()
            }
        }
    }