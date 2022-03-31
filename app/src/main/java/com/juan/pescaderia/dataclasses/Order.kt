package com.juan.pescaderia.dataclasses

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

@Parcelize
data class Order(
    @Expose @SerializedName("_id") @Field("_id") @ColumnInfo(name ="_id") var _id: String?,
    @Expose @SerializedName("date") @Field("date") @ColumnInfo(name ="date") var date: String,
    @Expose @SerializedName("order_id") @Field("order_id") @ColumnInfo(name ="order_id") var order_id: String,
    @Expose @SerializedName("cif_cliente") @Field("cif_cliente") @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "cif_cliente") val cif_cliente: String,
    @Expose @SerializedName("nombre_producto") @Field("nombre_producto") @ColumnInfo(name ="nombre_producto") val nombre_producto: String,
    @Expose @SerializedName("cantidad") @Field("cantidad") @ColumnInfo(name ="cantidad") var cantidad: Float,
    @Expose @SerializedName("precio") @Field("precio") @ColumnInfo(name ="precio") val precio: Float,
    @Expose @SerializedName("precio_total") @Field("precio_total") @ColumnInfo(name ="precio_total") var precio_total: Float,
    @Expose @SerializedName("id_producto") @Field("id_producto") @ColumnInfo(name ="id_producto") val id_producto: String
) : Parcelable
