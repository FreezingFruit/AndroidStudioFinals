package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityDashboardBinding
import com.example.mobcomfinals.databinding.ActivityHomePageBinding

class HomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            btnProfile.setOnClickListener {
            ProfileActivity.launch(this@HomePageActivity)
        }
            btnApartment.setOnClickListener {
                MainActivity.launch(this@HomePageActivity)
            }

            btnDuplex.setOnClickListener {
                DuplexListActivity.launch(this@HomePageActivity)
            }

            btnCabin.setOnClickListener {
                CabinListActivity.launch(this@HomePageActivity)
            }
        }

    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, HomePageActivity::class.java))
    }
}