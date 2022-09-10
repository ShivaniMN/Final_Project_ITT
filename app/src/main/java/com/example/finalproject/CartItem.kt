package com.example.finalproject

import android.os.Parcelable
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.versionedparcelable.ParcelField
import com.example.finalproject.Product
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem (
    var id: String? = "",
    var img: String? = "",
    var title: String? = "",
    var price: String? = "",
    var originalPrice: String? = "",
    var quantity: String? = ""
        ):Parcelable

