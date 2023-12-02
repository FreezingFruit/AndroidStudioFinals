package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Profile
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityProfileBinding
import com.example.mobcomfinals.model.ProfileModel
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private var imageUri: Uri? = null
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

        binding.ivUserProfile.setOnClickListener {
            resultLauncher.launch("image/*")

        }

    }

        private val resultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                imageUri = it
                binding.ivUserProfile.setImageURI(imageUri)

                val bitmap = (binding.ivUserProfile.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                val profile = ProfileModel(
                    authenticationViewModel.getUserUid(),
                    null,
                    binding.tvEmail.text.toString(),
                    binding.tvNumber.text.toString(),
                    binding.tvName.text.toString()
                )

                authenticationViewModel.updateUserProfile(baos.toByteArray(), profile)

            }


        private fun handleState(state: AuthenticationStates) {
            when (state) {
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
            fun launch(activity: Activity) =
                activity.startActivity(Intent(activity, ProfileActivity::class.java))
        }
    }


