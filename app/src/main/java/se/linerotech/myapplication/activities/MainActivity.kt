package se.linerotech.myapplication.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.linerotech.myapplication.R
import se.linerotech.myapplication.models.StopData
import se.linerotech.myapplication.repository.RetrofitClient
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set the title of the action bar
        supportActionBar?.title = getString(R.string.nearby_stops)
        checkLocationPermission()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            MY_LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    //Access the location of the user
                    getUserLocation()
                }else {
                    Toast.makeText(this, R.string.please_enable_location, Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                //Ignore all other request codes
            }
        }

    }

    private fun queryNearByStops(latitude: Double, longitude: Double) {
        RetrofitClient
            .instance
            .getNearbyStops(latitude, longitude)
            .enqueue(object: Callback<StopData> {
                override fun onFailure(call: Call<StopData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "I am onFailure", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<StopData>, response: Response<StopData>) {
                    Toast.makeText(this@MainActivity, "I am onResponse", Toast.LENGTH_LONG).show()
                }
            })

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //The location permission has been granted
            getUserLocation()
        }else {
            //Permission not granted --> Ask for permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_LOCATION_PERMISSION_CODE )
        }



    }

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                //Get the nearby stops from the user location
                queryNearByStops(it.latitude, it.longitude)

            }
            .addOnFailureListener{
                //Send a message to the user
                Toast.makeText(this, R.string.unable_to_get_user_location, Toast.LENGTH_LONG).show()
                Log.e("MainActivity", "Error: ${it.localizedMessage}")
            }
    }
    companion object {
        private const val MY_LOCATION_PERMISSION_CODE = 1000
    }


}
