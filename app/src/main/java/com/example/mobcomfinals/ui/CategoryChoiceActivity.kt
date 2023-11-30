package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityCategoryChoiceBinding
import com.example.mobcomfinals.databinding.ActivityHomePageBinding

class CategoryChoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryChoiceBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryChoiceBinding.inflate(layoutInflater)
        authenticationViewModel = AuthenticationViewModel()
        authenticationViewModel.getStates().observe(this@CategoryChoiceActivity) {
            handleState(it)
        }
        authenticationViewModel.getUserProfile()

        setContentView(binding.root)
        with(binding){
            btnProfile.setOnClickListener {
                com.example.mobcomfinals.ui.ProfileActivity.launch(this@CategoryChoiceActivity)
            }
            btnApartment.setOnClickListener {
                com.example.mobcomfinals.ui.MainActivity.launch(this@CategoryChoiceActivity)
            }

            btnDuplex.setOnClickListener {
                com.example.mobcomfinals.ui.DuplexListActivity.launch(this@CategoryChoiceActivity)
            }

            btnCabin.setOnClickListener {
                com.example.mobcomfinals.ui.CabinListActivity.launch(this@CategoryChoiceActivity)
            }
        }

    }

    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.Default -> {
                Glide.with(this)
                    .load(state.user?.profilePicture)
                    .centerCrop()
                    .into(binding.ivUserProfile)
                binding.tvWelcomeUser.text = "${state.user?.username}!"
            }
            AuthenticationStates.Error -> TODO()
            AuthenticationStates.LogOut -> {
                LoginActivity.launch(this@CategoryChoiceActivity)
                finish()
            }
            AuthenticationStates.UserDeleted -> {
                LoginActivity.launch(this@CategoryChoiceActivity)
                finish()
            }
            AuthenticationStates.VerificationEmailSent -> TODO()
            else -> {}
        }
    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, CategoryChoiceActivity::class.java))
    }
}