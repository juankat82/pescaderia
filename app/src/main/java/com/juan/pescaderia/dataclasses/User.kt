package com.juan.pescaderia.dataclasses

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

@Parcelize
data class User (
    @Expose @SerializedName("_id") @Field("_id") val _id:String?,
    @Expose @SerializedName("user_id") @Field("user_id") val user_id:String,
    @Expose @SerializedName("cif") @Field("cif") var cif:String,
    @Expose @SerializedName("name") @Field("name") var name:String,
    @Expose @SerializedName("surname") @Field("surname") var surname:String,
    @Expose @SerializedName("address") @Field("address") var address:String,
    @Expose @SerializedName("telephone") @Field("telephone") var telephone:String,
    @Expose @SerializedName("email") @Field("email") var email:String,
    @Expose @SerializedName("password") @Field("password") var password:String,
    @Expose @SerializedName("recover_question") @Field("recover_question") var recoverQuestion:String,
    @Expose @SerializedName("recover_answer") @Field("recover_answer") var recoverAnswer:String,
    @Expose @SerializedName("approved") @Field("approved") var approvedByAdmin:Boolean = true) : Parcelable