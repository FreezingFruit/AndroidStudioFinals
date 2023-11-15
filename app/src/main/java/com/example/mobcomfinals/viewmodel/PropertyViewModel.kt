package com.example.mobcomfinals.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobcomfinals.model.PropertyModel
import com.example.mobcomfinals.states.StorageStates
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PropertyViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val refProperty = database.getReference(NODE_PROPERTY)
    private var fb_storage = FirebaseStorage.getInstance().getReference(NODE_PROPERTY_IMAGES)
    private var state = MutableLiveData<StorageStates>()

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _property = MutableLiveData<PropertyModel>()
    val property: LiveData<PropertyModel> get() = _property

    fun addProperty(property: PropertyModel) {
        property.id = refProperty.push().key

        refProperty.child(property.id!!).setValue(property).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val property = snapshot.getValue(PropertyModel::class.java)
            property?.id = snapshot.key
            _property.value = property!!
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val property = snapshot.getValue(PropertyModel::class.java)
            property?.id = snapshot.key
            _property.value = property!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val property = snapshot.getValue(PropertyModel::class.java)
            property?.id = snapshot.key
            _property.value = property!!
            property?.isDeleted = true

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }

    fun getRealtimeUpdate(){
        refProperty.addChildEventListener(childEventListener)
    }

    fun updateProperty(property: PropertyModel){
        refProperty.child(property.id!!).setValue(property).addOnCompleteListener {
            if(it.isSuccessful){
                _result.value = null
            }else{
                _result.value = it.exception
            }
        }
    }

    fun deleteProperty(property: PropertyModel){
        refProperty.child(property.id!!).setValue(null).addOnCompleteListener {
            if(it.isSuccessful){
                _result.value = null
            }else{
                _result.value = it.exception
            }
        }
    }

    fun uploadImage(image_uri : Uri) : String{
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now) + ".jpg"

        fb_storage = fb_storage.child(fileName)

        image_uri.let {
            fb_storage.putFile(it)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        _result.value = null
                    } else {
                        _result.value = task.exception
                    }
                }
        }

        return fileName
    }

    fun getImageUrl(filename : String) {
        fb_storage.child("images/$filename.jpg").downloadUrl
            .addOnSuccessListener {
                state.value = StorageStates.GetUrlSuccess(it.toString())
            }.addOnFailureListener {
                state.value = StorageStates.StorageFailed(it.message)
            }
    }

    fun saveProfile(img : ByteArray, propertyName : String, propertyInformation : String, propertySeller:String, propertySellerNumber:String, propertyPrice:String) {
        val userRef = fb_storage.child("$propertyName.jpg")

        userRef.putBytes(img).addOnSuccessListener {
            userRef.downloadUrl.addOnSuccessListener {
                val property = PropertyModel(null, it.toString(), propertyName, propertyInformation, propertySeller, propertySellerNumber, propertyPrice, false)
                refProperty.child(propertyName).setValue(property)

            }.addOnFailureListener {
                state.value = StorageStates.StorageFailed(it.message)
            }

        }.addOnFailureListener {
            state.value = StorageStates.StorageFailed(it.message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        refProperty.removeEventListener(childEventListener)
    }
}