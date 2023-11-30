package com.example.mobcomfinals.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityEditPropertyBinding
import com.example.mobcomfinals.model.PropertyModel
import com.example.mobcomfinals.viewmodel.PropertyViewModel
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class EditPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPropertyBinding
    private lateinit var viewModel: PropertyViewModel
    private var imageUri : Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPropertyBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PropertyViewModel::class.java]
        val property = intent.getParcelableExtra<PropertyModel>("property")
        val position = intent.getStringExtra("position" )

        binding.inputPropertyName.setText(property?.propertyName)
        binding.inputPropertyDescription.setText(property?.propertyInformation)
        binding.inputPropertyEmailSeller.setText(property?.propertySeller)
        binding.inputPropertyNumberSeller.setText(property?.propertySellerNumber)
        binding.inputPropertyPrice.setText(property?.propertyPrice)
        binding.inputPropertyLocation.setText(property?.propertyLocation)

        Glide.with(this)
            .load(property?.propertyPicture)
            .centerCrop()
            .into(binding.ivSelectedImage)

        binding.btnAddImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.btnEditProperty.setOnClickListener{
            val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            var status = true

            if(binding.inputPropertyName.text.isNullOrBlank()){
                status = false
                binding.inputPropertyName.error = "Empty field"
            }
            if(binding.inputPropertyBedroom.text.isNullOrBlank()){
                status = false
                binding.inputPropertyBedroom.error = "Empty field"
            }
            if(binding.inputPropertyBathroom.text.isNullOrBlank()){
                status = false
                binding.inputPropertyBathroom.error = "Empty field"
            }
            if(binding.inputPropertyDescription.text.isNullOrBlank()){
                status = false
                binding.inputPropertyDescription.error = "Empty field"
            }
            if(binding.inputPropertyEmailSeller.text.isNullOrBlank()){
                status = false
                binding.inputPropertyEmailSeller.error = "Empty field"
            }
            if(binding.inputPropertyNumberSeller.text.isNullOrBlank()){
                status = false
                binding.inputPropertyNumberSeller.error = "Empty field"
            }
            if(binding.inputPropertyPrice.text.isNullOrBlank()){
                status = false
                binding.inputPropertyPrice.error = "Empty field"
            }

            if(binding.inputPropertyLocation.text.isNullOrBlank()){
                status = false
                binding.inputPropertyLocation.error = "Empty field"
            }

            if (status){
                if (restrictions()){
                    property?.propertyCategory
                    property?.propertyName = binding.inputPropertyName.text.toString()
                    property?.propertyBedrooms = binding.inputPropertyBedroom.text.toString()
                    property?.propertyBathrooms = binding.inputPropertyBedroom.text.toString()
                    property?.propertyInformation = binding.inputPropertyDescription.text.toString()
                    property?.propertySeller = binding.inputPropertyEmailSeller.text.toString()
                    property?.propertySellerNumber = binding.inputPropertyNumberSeller.text.toString()
                    property?.propertyPrice = binding.inputPropertyPrice.text.toString()
                    property?.propertyLocation = binding.inputPropertyLocation.text.toString()


                    viewModel.updateProperty(baos.toByteArray(),property!!)

                    startActivity(Intent(this, HomePageActivity::class.java))
                }
            }
        }


    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.ivSelectedImage.setImageURI(imageUri)


    }

    private fun restrictions(): Boolean {
        if (binding.inputPropertyNumberSeller.text.toString().length != 11){

            binding.inputPropertyNumberSeller.error = "Phone number should be 11 digits long"
            return false
        }
        if(binding.inputPropertyEmailSeller.text.toString().isNotEmpty()){
            val pattern: Pattern = Pattern.compile("@.+")
            val matcher = pattern.matcher(binding.inputPropertyEmailSeller.text.toString())

            if (matcher.find()){
                return true
            }

            binding.inputPropertyEmailSeller.error = "Email must have \"@\" followed by the email service!"
            return false
        }

        return true
    }
}