package se.linerotech.myapplication.models


import com.google.gson.annotations.SerializedName

data class DepartureBoard(
    @SerializedName("Departure")
    val departure: List<Departure> = listOf(),
    @SerializedName("noNamespaceSchemaLocation")
    val noNamespaceSchemaLocation: String = "",
    @SerializedName("serverdate")
    val serverdate: String = "",
    @SerializedName("servertime")
    val servertime: String = ""
)