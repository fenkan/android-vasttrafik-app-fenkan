package se.linerotech.myapplication.models


import com.google.gson.annotations.SerializedName

data class StopLocation(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("lat")
    val lat: String = "",
    @SerializedName("lon")
    val lon: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("track")
    val track: String = ""
)