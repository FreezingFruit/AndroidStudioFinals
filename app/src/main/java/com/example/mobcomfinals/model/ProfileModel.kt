package com.example.mobcomfinals.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize()
data class ProfileModel(
    @get:Exclude
    var id : String? = null,

    var profilePicture: String? = null,
    var email : String? = null,
    var contactNumber : String? = null,
    var username : String? = null

    ):Parcelable{
    override fun equals(other: Any?): Boolean {
        return if(other is PropertyModel){
            other.id == id
        }else false
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (contactNumber?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }
    }
