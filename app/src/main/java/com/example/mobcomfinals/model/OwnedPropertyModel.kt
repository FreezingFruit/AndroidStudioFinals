package com.example.mobcomfinals.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize()
data class OwnedPropertyModel(
    var propertyKey: String? = null,
    var category: String? = null
) : Parcelable
