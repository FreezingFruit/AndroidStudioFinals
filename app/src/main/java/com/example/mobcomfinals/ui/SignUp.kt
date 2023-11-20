package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel : AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthenticationViewModel()
        viewModel.getStates().observe(this@SignUp) {
            handleState(it)
        }

        binding.btnSignup.setOnClickListener {
            viewModel.signUp(
                binding.tieEmail.text.toString(),
                binding.tiePassword.text.toString()
            )
        }

    }

    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.SignedUp -> viewModel.createUserRecord(

                binding.tieEmail.text.toString(),
                binding.tieName.text.toString(),
                binding.tieContactNumber.toString())


            is AuthenticationStates.ProfileUpdated -> {
                LoginActivity.launch(this@SignUp)
                finish()
            }
            else -> {}
        }
    }


    companion object {
        fun launch(activity: Activity) = activity.startActivity(Intent(activity, SignUp::class.java))
    }
}