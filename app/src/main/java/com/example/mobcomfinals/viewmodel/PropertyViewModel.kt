package com.example.mobcomfinals.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobcomfinals.adapter.PropertyAdapter
import com.example.mobcomfinals.model.CategoriesModel
import com.example.mobcomfinals.model.OwnedPropertyModel
import com.example.mobcomfinals.model.PropertyModel
import com.example.mobcomfinals.states.StorageStates
import com.example.mobcomfinals.ui.AuthenticationViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private val refUser = database.getReference(NODE_USER)
    private val authUser = AuthenticationViewModel()


    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _property = MutableLiveData<PropertyModel>()
    private val _category = MutableLiveData<CategoriesModel>()
    val property: LiveData<PropertyModel> get() = _property
    val category: LiveData<CategoriesModel> get() = _category

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
            checkIfCurrentUserProperty(property?.id!!).addOnSuccessListener { exists ->
                if(exists){

                    Log.d("test123","it exists")
                } else{
                    Log.d("test123","nono zone")
                    _property.value = property!!
                }
            }
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

    private val ownedPropertyChildEventListener = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val property = snapshot.getValue(PropertyModel::class.java)
            property?.id = snapshot.key
            checkIfCurrentUserProperty(property?.id!!).addOnSuccessListener { exists ->
                if(exists){
                    _property.value = property!!
                    Log.d("test123","it exists")
                } else{
                    Log.d("test123","nono zone")

                }
            }
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

    fun getRealtimeUpdate(category: String){
        refProperty.child(category).addChildEventListener(childEventListener)
    }

    fun getRealtimeUpdateOwnedProperty(category: String){

        refProperty.child(category).addChildEventListener(ownedPropertyChildEventListener)

    }

    fun updateProperty(img:ByteArray, property: PropertyModel){
        val propertyRef = fb_storage.child("${property.id!!}.jpg")

        propertyRef.putBytes(img).addOnSuccessListener {
            propertyRef.downloadUrl.addOnSuccessListener { uri ->
                property.id?.let {
                    val updatedProperty = PropertyModel(it, property.propertyCategory, property.propertyPicture, property.propertyName, property.propertyBedrooms,property.propertyBathrooms, property.propertyInformation, property.propertySeller, property.propertySellerNumber, property.propertyPrice, property.propertyLocation, false)
                    refProperty.child(property.propertyCategory!!).child(it).setValue(updatedProperty)

                }
            }
        }
    }

    fun deleteProperty(property: PropertyModel){
        refProperty.child(property.propertyCategory!!).child(property.id!!).setValue(null).addOnCompleteListener {
            if(it.isSuccessful){
                _result.value = null
            }else{
                _result.value = it.exception
            }
        }

        refUser.child(authUser.getUserUid()!!).child(NODE_OWNEDPROPERTY).child(property.id!!).setValue(null).addOnCompleteListener {
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

    fun saveProperty(img : ByteArray, propertyCategory:String, propertyName : String,propertyBedrooms:String, propertyBathrooms:String, propertyInformation : String, propertySeller:String, propertySellerNumber:String, propertyPrice:String, propertyLocation:String, userUid: String) {
        val newKey = refProperty.push().key

        val propertyRef = fb_storage.child("$newKey.jpg")


        propertyRef.putBytes(img).addOnSuccessListener {
            propertyRef.downloadUrl.addOnSuccessListener {
                val property = PropertyModel(null, propertyCategory , it.toString(), propertyName,propertyBedrooms,propertyBathrooms, propertyInformation, propertySeller, propertySellerNumber, propertyPrice, propertyLocation, false)
                refProperty.child(propertyCategory).child(newKey!!).setValue(property)

                val ownedPropertyModel = OwnedPropertyModel(newKey, propertyCategory)

                refUser.child(userUid).child(NODE_OWNEDPROPERTY).child(newKey).setValue(ownedPropertyModel)



            }
        }

    }

    fun getCategories(callback:(List<CategoriesModel>) -> Unit){
        val refCategory = refProperty.child(NODE_CATEGORIES)

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val categoriesList:MutableList<CategoriesModel> = mutableListOf()
                for (categoriesSnapShot in dataSnapshot.children){
                    val category = categoriesSnapShot.getValue(CategoriesModel::class.java)
                    categoriesList.add(category!!)
                }
                callback(categoriesList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }

        }

        refCategory.addValueEventListener(valueEventListener)

    }

    fun checkIfCurrentUserProperty(propertyId:String):Task<Boolean>{
        val refUserProperties = refUser.child(authUser?.getUserUid()!!).child(NODE_OWNEDPROPERTY)
            .child(propertyId).child("propertyKey")

        return refUserProperties.get().continueWith { task ->
            if (task.isSuccessful) {

                val dataSnapshot = task.result
                Log.d("test123","$dataSnapshot")
                val ownedProperty = dataSnapshot.getValue()
                Log.d("test123", "$ownedProperty")
                if (ownedProperty == propertyId) {
                    return@continueWith true
                    Log.d("test123","pagnakita mo ako crush ka ng crush mo")

                }

//                for (ownedPropertySnapShot in dataSnapshot.children) {
//
//                }
                false
            } else {
                Log.e("test12", "Error getting property", task.exception)
                false
            }
        }
    }

    fun checkIfCurrentUserProperty2(propertyId:String):Task<Boolean>{
        val refUserProperties = refUser.child(authUser?.getUserUid()!!).child(NODE_OWNEDPROPERTY)
            .child(propertyId).child("propertyKey")

        Log.d("test123", "$refUserProperties")

        return refUserProperties.get().continueWith { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                val ownedProperty = dataSnapshot.getValue(OwnedPropertyModel::class.java)
                if (ownedProperty?.propertyKey == propertyId) {
                    return@continueWith true
                    Log.d("test123", "$ownedProperty")
                }

//                for (ownedPropertySnapShot in dataSnapshot.children) {
//
//                }
                false
            } else {
                Log.e("test12", "Error getting property", task.exception)
                false
            }
        }
    }

//    fun getOwnedPropertyCategory():Task<Boolean>{
//        val refCategory = refUser.child(authUser.getUserUid()!!).child(NODE_OWNEDPROPERTY)
//        val ownedPropertyList : MutableList<OwnedPropertyModel> = mutableListOf()
//        val refProperties = refProperty
//        refCategory.get().continueWith { task ->
//
//            if (task.isSuccessful) {
//                val dataSnapshot = task.result
//
//                for (ownedPropertySnapShot in dataSnapshot.children) {
//                    val ownedProperty = dataSnapshot.getValue(OwnedPropertyModel::class.java)
//                    ownedPropertyList.add(ownedProperty!!)
//
//                    refCategory.child()
//
//                }
//
//            }
//            true
//        }
//        return refProperty.child()
//    }

    override fun onCleared() {
        super.onCleared()
        refProperty.removeEventListener(childEventListener)
    }
}