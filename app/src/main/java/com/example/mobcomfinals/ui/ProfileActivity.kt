package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        authenticationViewModel = AuthenticationViewModel()
        setContentView(binding.root)

        authenticationViewModel = AuthenticationViewModel()
        authenticationViewModel.getStates().observe(this@ProfileActivity) {
            handleState(it)
        }
        authenticationViewModel.getUserProfile()

        binding.btnCheckProperties.setOnClickListener {
            startActivity(Intent(this, OwnedPropertyActivity::class.java))
        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            authenticationViewModel.logOut()
        }




    }


    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.Default -> {
                Glide.with(this)
                    .load(state.user?.profilePicture)
                    .centerCrop()
                    .into(binding.ivUserProfile)

                binding.tvName.text = " ${state.user?.username}!"
                binding.tvEmail.text = " ${state.user?.email}!"
                binding.tvNumber.text = "${state.user?.contactNumber}!"
            }
            AuthenticationStates.Error -> TODO()
            AuthenticationStates.LogOut -> {
                LoginActivity.launch(this@ProfileActivity)
                finish()
            }
            AuthenticationStates.UserDeleted -> {
                LoginActivity.launch(this@ProfileActivity)
                finish()
            }
            AuthenticationStates.VerificationEmailSent -> TODO()
            else -> {}
        }
    }




    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, ProfileActivity::class.java))
    }
}