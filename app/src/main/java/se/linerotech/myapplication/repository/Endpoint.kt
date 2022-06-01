package se.linerotech.myapplication.repository


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import se.linerotech.myapplication.models.BusData
import se.linerotech.myapplication.models.StopData
import se.linerotech.myapplication.util.Credential

interface Endpoint {

    @Headers("Authorization: ${Credential.VASTTRAFIK_KEY}")
    @GET("location.nearbystops")
    fun getNearbyStops(
        @Query("originCoordLat") latitude: Double,
        @Query("originCoordLong") longitude: Double,
        @Query("format") format: String = "json"
    ): Call<StopData>

    @Headers("Authorization: ${Credential.VASTTRAFIK_KEY}")
    @GET("departureBoard")
    fun getDepartureTimeFromStops(
        @Query("id") stopId: Long,
        @Query("date") date: String,
        @Query("time") time: String,
        @Query("format") format: String = "json"
    ): Call<BusData>



}