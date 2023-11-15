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

class PropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: PropertyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val property = intent.getParcelableExtra<PropertyModel>("property")
        val position = intent.getIntExtra("position", 0)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PropertyViewModel::class.java]

        binding.reTitle.text = property?.propertyName
        binding.reDesc.text = property?.propertyInformation
        binding.reSeller.text = property?.propertySeller
        binding.rePrice.text = property?.propertyPrice

        Glide.with(this)
            .load(property?.propertyPicture)
            .centerCrop()
            .into(binding.reImage)

        setContentView(binding.root)

        binding.reSeller.setOnLongClickListener {
            val email = binding.reSeller.text.toString()
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            startActivity(emailIntent)
            true
        }

        binding.reSellerNum.setOnLongClickListener {
            val phone = binding.reSellerNum.text.toString()
            val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(phoneIntent)
            true
        }

        binding.btnEditProperty.setOnClickListener {
            val intent = Intent(this, EditPropertyActivity::class.java)
            intent.putExtra("property", property)
            intent.putExtra("position", position)
            startActivity(intent)
        }

    }
}