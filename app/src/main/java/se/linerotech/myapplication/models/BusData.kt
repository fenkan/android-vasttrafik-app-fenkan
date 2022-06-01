package se.linerotech.myapplication.models


import com.google.gson.annotations.SerializedName

data class BusData(
    @SerializedName("DepartureBoard")
    val departureBoard: DepartureBoard = DepartureBoard()
)