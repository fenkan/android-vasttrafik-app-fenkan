package se.linerotech.myapplication.models



import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
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
): Parcelable