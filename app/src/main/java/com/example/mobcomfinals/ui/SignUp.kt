package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivitySignUpBinding
import java.io.ByteArrayOutputStream

class SignUp : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel : AuthenticationViewModel
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthenticationViewModel()




        binding.btnAddImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.btnSignup.setOnClickListener {
            val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)


            viewModel.signUp(

                binding.tieEmail.text.toString(),
                binding.tiePassword.text.toString(),
            )

            viewModel.getStates().observe(this@SignUp) {
                handleState(it, baos.toByteArray())
            }
        }

    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.ivSelectedImage.setImageURI(imageUri)

    }

    private fun handleState(state : AuthenticationStates, baos:ByteArray) {
        when(state) {

            is AuthenticationStates.SignedUp -> viewModel.createUserRecord(

                baos,
                binding.tieEmail.text.toString(),
                binding.tieContactNumber.text.toString(),
                binding.tieName.text.toString())


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