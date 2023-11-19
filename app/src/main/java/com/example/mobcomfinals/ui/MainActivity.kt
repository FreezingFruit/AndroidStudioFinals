package com.example.mobcomfinals.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobcomfinals.R
import com.example.mobcomfinals.adapter.PropertyAdapter
import com.example.mobcomfinals.databinding.ActivityDashboardBinding
import com.example.mobcomfinals.databinding.ActivityMainBinding
import com.example.mobcomfinals.viewmodel.PropertyViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var viewModel: PropertyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PropertyViewModel::class.java]
        propertyAdapter = PropertyAdapter(this, ArrayList(), viewModel)
        binding.recyclerViewRealEstate.adapter = propertyAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRealEstate.layoutManager = layoutManager


        viewModel.property.observe(this, Observer {
            propertyAdapter.addProperty(it)
        })
        viewModel.getRealtimeUpdate()

        binding.btnAddProperty.setOnClickListener {
            startActivity(Intent(this, AddPropertyActivity::class.java))
        }
    }
}