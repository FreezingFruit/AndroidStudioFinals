package com.example.mobcomfinals.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityDetailsBinding
import com.example.mobcomfinals.model.PropertyModel
import com.example.mobcomfinals.viewmodel.PropertyViewModel
import android.R.*
import android.view.View
import android.widget.Toast

class PropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: PropertyViewModel
    private lateinit var authViewModel : AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val property = intent.getParcelableExtra<PropertyModel>("property")
        val position = intent.getIntExtra("position", 0)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PropertyViewModel::class.java]

        //-------------------------------
        authViewModel = AuthenticationViewModel()
        authViewModel.getStates().observe(this@PropertyActivity) {
            handleState(it)
        }
        authViewModel.getUserProfile()
        //-------------------------------

        val enteredSeller = property?.propertySeller
        val currentUserEmail = authViewModel.getUserEmail()


        binding.reTitle.text = property?.propertyName
        binding.reDesc.text = property?.propertyInformation
        binding.reSeller.text = property?.propertySeller
        binding.reCategory.text = property?.propertyCategory
        binding.reBedroom.text = property?.propertyBedrooms
        binding.reBathroom.text = property?.propertyBathrooms
        binding.reSellerNum.text = property?.propertySellerNumber
        binding.rePrice.text = property?.propertyPrice
        binding.reLocation.text = property?.propertyLocation

        Glide.with(this)
            .load(property?.propertyPicture)
            .centerCrop()
            .into(binding.reImage)

        setContentView(binding.root)

        binding.reSeller.setOnClickListener {
            val email = binding.reSeller.text.toString()
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(emailIntent)
            true
        }

        binding.reSellerNum.setOnClickListener {
            val phone = binding.reSellerNum.text.toString()
            val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(phoneIntent)
            true
        }


        if(enteredSeller == currentUserEmail){
            binding.btnEditProperty.visibility = View.VISIBLE
            binding.btnDeleteProperty.visibility = View.VISIBLE
        }
        binding.btnEditProperty.setOnClickListener {
            val intent = Intent(this, EditPropertyActivity::class.java)
            intent.putExtra("property", property)
            intent.putExtra("position", position)
            startActivity(intent)
        }

        binding.btnDeleteProperty.setOnClickListener {
            if (property != null){
                val intent = Intent(this, HomePageActivity::class.java)
                deleteItem(property)
                startActivity(intent)
            }
        }
        //PUT THE INVIS EDIT IF NEEDED
        //COMPARE WRITTEN EMAIL IF IS == SHOW IT IF != DONT SHOW IT
    }


    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.Default -> {

            }
            AuthenticationStates.Error -> TODO()
            AuthenticationStates.LogOut -> {
                LoginActivity.launch(this@PropertyActivity)
                finish()
            }
            AuthenticationStates.UserDeleted -> {
                LoginActivity.launch(this@PropertyActivity)
                finish()
            }
            AuthenticationStates.VerificationEmailSent -> TODO()
            else -> {}
        }
    }

    fun deleteItem(property: PropertyModel) {
        viewModel.deleteProperty(property)
    }

}