package com.example.mobcomfinals.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobcomfinals.R
import com.example.mobcomfinals.adapter.PropertyAdapter
import com.example.mobcomfinals.databinding.ActivityDashboardBinding
import com.example.mobcomfinals.databinding.ActivityOwnedPropertyBinding
import com.example.mobcomfinals.viewmodel.PropertyViewModel

class OwnedPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOwnedPropertyBinding
    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var propertyViewModel: PropertyViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnedPropertyBinding.inflate(layoutInflater)

        setContentView(binding.root)

        authenticationViewModel = AuthenticationViewModel()
        authenticationViewModel.getStates().observe(this@OwnedPropertyActivity) {
            handleState(it)
        }
        authenticationViewModel.getUserProfile()


        //-----------------------------------------------------------
        propertyViewModel = ViewModelProvider(this)[PropertyViewModel::class.java]
        propertyAdapter = PropertyAdapter(this, ArrayList())
        binding.recyclerViewRealEstate.adapter = propertyAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRealEstate.layoutManager = layoutManager


        propertyViewModel.property.observe(this, Observer {
            propertyAdapter.addProperty(it)
        })
        propertyViewModel.getCategories { categories ->
            for (category in categories){
                propertyViewModel.getRealtimeUpdateOwnedProperty(category.categoryName!!)


            }
        }
//        propertyViewModel.getRealtimeUpdateOwnedProperty("Apartment")
        //----------------------------------------------------------------




        binding.btnAddProperty.setOnClickListener {
            startActivity(Intent(this, AddPropertyActivity::class.java))
        }


    }

    private fun handleState(state : AuthenticationStates) {
        when(state) {
            is AuthenticationStates.Default -> {

            }
            AuthenticationStates.Error -> TODO()
            AuthenticationStates.LogOut -> {
                LoginActivity.launch(this@OwnedPropertyActivity)
                finish()
            }
            AuthenticationStates.UserDeleted -> {
                LoginActivity.launch(this@OwnedPropertyActivity)
                finish()
            }
            AuthenticationStates.VerificationEmailSent -> TODO()
            else -> {}
        }
    }

    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, OwnedPropertyActivity::class.java))
    }
}