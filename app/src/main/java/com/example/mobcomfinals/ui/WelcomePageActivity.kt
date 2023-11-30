package com.example.mobcomfinals.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityWelcomePageBinding
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class WelcomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomePageBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var gso : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomePageBinding.inflate(layoutInflater)
        authenticationViewModel = AuthenticationViewModel()
        setContentView(binding.root)

        authenticationViewModel.getStates().observe(this@WelcomePageActivity){

        }

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }

}