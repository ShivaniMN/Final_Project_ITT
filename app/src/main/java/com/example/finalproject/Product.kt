package com.example.finalproject

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    var description: String?,
    var img: String,
    var title: String,
    var price: String
    ): Parcelable