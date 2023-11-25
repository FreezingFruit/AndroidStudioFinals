package com.example.mobcomfinals.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobcomfinals.databinding.ActivityDetailsBinding
import com.example.mobcomfinals.databinding.ActivityPropertyBinding
import com.example.mobcomfinals.model.PropertyModel
import com.example.mobcomfinals.ui.AuthenticationViewModel
import com.example.mobcomfinals.ui.PropertyActivity
import com.example.mobcomfinals.viewmodel.PropertyViewModel

class PropertyAdapter(
    private val context: Context,
    private var propertyList: MutableList<PropertyModel> = mutableListOf(),
    private var viewModel : PropertyViewModel,

    ) : RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ActivityPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, binding, viewModel)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        propertyList[position].let {
            holder.bind(it, position)
        }
    }

    fun addProperty(property: PropertyModel){
        if(!propertyList.contains(property)){
            propertyList.add(property)
        }else{
            val index = propertyList.indexOf(property)
            if(property.isDeleted == true){
                propertyList.removeAt(index)
            } else {
                propertyList[index] = property
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(

        private val context: Context,
        private val binding: ActivityPropertyBinding,
        private var viewModel: PropertyViewModel

    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(property: PropertyModel, position: Int) {
            binding.reTitle.text = property.propertyName
            binding.reDesc.text = property.propertyInformation
            binding.reSeller.text = property.propertySeller
            binding.reSellerNum.text = property.propertySellerNumber
            binding.rePrice.text = property.propertyPrice
            binding.reLocation.text = property.propertyLocation


            Glide.with(context)
                .load(property.propertyPicture)
                .centerCrop()
                .into(binding.reImage)

            binding.rvPropertyDesignList.setOnClickListener {
                val intent = Intent(context, PropertyActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("property", property)
                context.startActivity(intent)
            }

        }

    }
}

