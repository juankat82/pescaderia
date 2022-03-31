package com.juan.pescaderia.dataclasses

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Email (val from:String, val to:String, val subject:String, val emailBody:String) : Parcelable