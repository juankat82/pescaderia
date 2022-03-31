package com.juan.pescaderia.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TextMessage (val fromNumber:String, val toNumber:String, val textBody:String) : Parcelable