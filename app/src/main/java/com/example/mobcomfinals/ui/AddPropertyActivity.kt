package com.example.mobcomfinals.ui
import android.R
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.health.connect.datatypes.units.Length
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

import com.example.mobcomfinals.databinding.ActivityAddPropertyBinding
import com.example.mobcomfinals.model.CategoriesModel
import com.example.mobcomfinals.viewmodel.PropertyViewModel
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class AddPropertyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPropertyBinding
    private lateinit var viewModel: PropertyViewModel
    private lateinit var authViewModel: AuthenticationViewModel
    private var imageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPropertyBinding.inflate(layoutInflater)
        authViewModel = AuthenticationViewModel()
        val currentUserEmail = authViewModel.getUserEmail()
        val currentUserNum = authViewModel.getUserNum()
        var selectedCategory = ""

        setContentView(binding.root)


        authViewModel.getStates().observe(this@AddPropertyActivity) {
            handleState(it)
        }
        authViewModel.getUserProfile()

        viewModel = ViewModelProvider(this)[PropertyViewModel::class.java]

        //---------------------------------------------------------------
        viewModel.getCategories{ categoriesModels ->
            val items = mutableListOf("Select...")
            items.addAll(categoriesModels.map { it?.categoryName?:"" })

            val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, items)
            binding.propertyCategory.adapter = adapter
        }



        //-------------------------------------------------------------------

        binding.inputPropertyEmailSeller.setText(currentUserEmail)
        binding.inputPropertyNumberSeller.setText(currentUserNum)


        binding.btnAddImage.setOnClickListener{
            resultLauncher.launch("image/*")
        }


        //---------------------------------------------
        binding.propertyCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = binding.propertyCategory.selectedItem.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected if needed
            }
        }
        //----------------------------------------------------



        binding.btnAddProperty.setOnClickListener{
            val bitmap = (binding.ivSelectedImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            var status = true
            if(selectedCategory.isEmpty() || selectedCategory == "Select..."){
                status = false
                Toast.makeText(this, "Bonakid ka ba? Kaya ka iniwan eh", Toast.LENGTH_SHORT).show()
            }

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
                    viewModel.saveProperty(
                        baos.toByteArray(),
                        selectedCategory,
                        binding.inputPropertyName.text.toString(),
                        binding.inputPropertyBedroom.text.toString(),
                        binding.inputPropertyBathroom.text.toString(),
                        binding.inputPropertyDescription.text.toString(),
                        binding.inputPropertyEmailSeller.text.toString(),
                        binding.inputPropertyNumberSeller.text.toString(),
                        binding.inputPropertyPrice.text.toString(),
                        binding.inputPropertyLocation.text.toString(),
                        authViewModel.getUserUid().toString()
                    )


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


    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.Default -> {
                binding.inputPropertyNumberSeller.setText(state.user?.contactNumber)
            }
            AuthenticationStates.Error -> TODO()
            AuthenticationStates.LogOut -> {
                LoginActivity.launch(this@AddPropertyActivity)
                finish()
            }
            AuthenticationStates.UserDeleted -> {
                LoginActivity.launch(this@AddPropertyActivity)
                finish()
            }
            AuthenticationStates.VerificationEmailSent -> TODO()
            else -> {}
        }
    }
    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, AddPropertyActivity::class.java))
    }
}