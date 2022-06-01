package se.linerotech.myapplication.models


import com.google.gson.annotations.SerializedName

data class Departure(
    @SerializedName("accessibility")
    val accessibility: String = "",
    @SerializedName("bgColor")
    val bgColor: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("direction")
    val direction: String = "",
    @SerializedName("fgColor")
    val fgColor: String = "",
    @SerializedName("JourneyDetailRef")
    val journeyDetailRef: JourneyDetailRef = JourneyDetailRef(),
    @SerializedName("journeyNumber")
    val journeyNumber: String = "",
    @SerializedName("journeyid")
    val journeyid: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("sname")
    val sname: String = "",
    @SerializedName("stop")
    val stop: String = "",
    @SerializedName("stopid")
    val stopid: String = "",
    @SerializedName("stroke")
    val stroke: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("track")
    val track: String = "",
    @SerializedName("type")
    val type: String = ""
)