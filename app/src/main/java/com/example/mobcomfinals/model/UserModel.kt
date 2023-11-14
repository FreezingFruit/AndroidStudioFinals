package com.example.mobcomfinals.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize()
data class UserModel(
    @get:Exclude
    var id : String? = null,

    var profilePicture: String? = null,
    var email : String? = null,
    var contactNumber : String? = null,
    var username : String? = null


) : Parcelable
