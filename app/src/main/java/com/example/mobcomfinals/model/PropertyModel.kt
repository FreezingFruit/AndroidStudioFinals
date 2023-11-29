package com.example.mobcomfinals.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize()
data class PropertyModel(
    @get:Exclude
    var id: String? = null,
    var propertyCategory: String? = null,
    var propertyPicture: String? = null,
    var propertyName: String? = null,
    var propertyBedrooms: String? = null,
    var propertyBathrooms: String? = null,
    var propertyInformation: String? = null,
    var propertySeller: String? = null,
    var propertySellerNumber: String? = null,
    var propertyPrice: String? = null,
    var propertyLocation: String? = null,

    @get:Exclude
    var isDeleted: Boolean? = null

) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return if(other is PropertyModel){
            other.id == id
        }else false
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (propertyPicture?.hashCode() ?: 0)
        result = 31 * result + (propertyName?.hashCode() ?: 0)
        result = 31 * result + (propertyInformation?.hashCode() ?: 0)
        result = 31 * result + (propertySeller?.hashCode() ?: 0)
        result = 31 * result + (propertySellerNumber?.hashCode() ?: 0)
        result = 31 * result + (propertyPrice?.hashCode() ?: 0)
        result = 31 * result + (propertyLocation?.hashCode() ?: 0)
        result = 31 * result + isDeleted.hashCode()
        return result
    }
}
