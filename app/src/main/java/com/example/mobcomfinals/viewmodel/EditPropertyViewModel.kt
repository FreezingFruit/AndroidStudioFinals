package com.example.mobcomfinals.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobcomfinals.model.PropertyModel

class EditPropertyViewModel:ViewModel() {
    val updatedProperty = MutableLiveData<PropertyModel>()
}