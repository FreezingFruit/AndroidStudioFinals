package com.example.mobcomfinals.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.mobcomfinals.R
import com.example.mobcomfinals.databinding.ActivityAddPropertyBinding
import com.example.mobcomfinals.viewmodel.PropertyViewModel
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class AddPropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPropertyBinding
    private lateinit var viewModel: PropertyViewModel
    private var imageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPropertyBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PropertyViewModel::class.java]

        binding.btnAddImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.btnAddProperty.setOnClickListener{
            val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            var status = true
            if(binding.inputPropertyName.text.isNullOrBlank()){
                status = false
                binding.inputPropertyName.error = "Empty field"
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

            if (status){
                if (restrictions()){
                    viewModel.saveProfile(
                        baos.toByteArray(),
                        binding.inputPropertyName.text.toString(),
                        binding.inputPropertyDescription.text.toString(),
                        binding.inputPropertyEmailSeller.text.toString(),
                        binding.inputPropertyNumberSeller.text.toString(),
                        binding.inputPropertyPrice.text.toString(),
                        binding.inputPropertyLocation.text.toString()
                    )
                    startActivity(Intent(this, MainActivity::class.java))
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