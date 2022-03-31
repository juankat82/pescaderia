package com.juan.pescaderia.dataclasses

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field


@Entity(tableName = "product") //This is a Room annotation
@Parcelize
data class Product(
    @Expose @SerializedName("_id") @Field("_id") @ColumnInfo(name = "product_id") var _id: String?,
    @Expose @SerializedName("foreign_id") @Field("foreign_id")  @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "foreign_id") @NonNull var foreign_id : String,
    @Expose @SerializedName("nombre") @Field("nombre") @ColumnInfo(name = "nombre") var nombre : String,
    @Expose @SerializedName("peso_kg") @Field("peso_kg") @ColumnInfo(name = "peso_kg") var peso_kg : Boolean,
    @Expose @SerializedName("precio") @Field("precio") @ColumnInfo(name = "precio") var precio : Float,
    @Expose @SerializedName("unidad") @Field("unidad") @ColumnInfo(name = "unidad") var unidad : Boolean) : Parcelable

