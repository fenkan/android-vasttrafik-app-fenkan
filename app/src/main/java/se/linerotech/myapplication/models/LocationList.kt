package se.linerotech.myapplication.models


import com.google.gson.annotations.SerializedName

data class LocationList(
    @SerializedName("noNamespaceSchemaLocation")
    val noNamespaceSchemaLocation: String = "",
    @SerializedName("serverdate")
    val serverdate: String = "",
    @SerializedName("servertime")
    val servertime: String = "",
    @SerializedName("StopLocation")
    val stopLocation: List<StopLocation> = listOf()
)