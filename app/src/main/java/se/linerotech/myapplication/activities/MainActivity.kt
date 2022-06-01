package se.linerotech.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.linerotech.myapplication.R
import se.linerotech.myapplication.adapters.StopsRecyclerViewAdapter
import se.linerotech.myapplication.models.StopData
import se.linerotech.myapplication.models.StopLocation
import se.linerotech.myapplication.repository.RetrofitClient

class MainActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    private var userLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set the title of the action bar
        supportActionBar?.title = getString(R.string.nearby_stops)

        // Initialize variables
        progressBar = findViewById(R.id.mainActivityProgressBar)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.map -> {
                startActivity(Intent(this, MapActivity::class.java))
                true
            }
            else -> {
                false
            }
        }
    }

    private fun queryNearByStops(latitude: Double, longitude: Double) {
        RetrofitClient
            .instance
            .getNearbyStops(latitude, longitude)
            .enqueue(object: Callback<StopData> {
                override fun onFailure(call: Call<StopData>, t: Throwable) {
                    // Notify the user
                    Toast.makeText(this@MainActivity,
                        R.string.unable_to_retrieve_stops,
                        Toast.LENGTH_LONG
                    ).show()

                    // Report the error on the logcat
                    Log.e(TAG, "Error: ${t.localizedMessage}")
                }

                override fun onResponse(call: Call<StopData>, response: Response<StopData>) {
                    if (response.isSuccessful) {
                        // Show the items to the user
                        val filteredListOfStops = response.body()?.locationList?.stopLocation?.distinctBy { it.name }
                            ?: emptyList()
                        showItems(filteredListOfStops)

                    }else {
                        // Determine the reason of failure
                      val message = when(response.code()) {
                          401 -> R.string.update_api_key
                          500 -> R.string.internal_server_error
                          else -> R.string.unable_to_retrieve_stops
                      }
                        // Notify the user the reason of the failure
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Error: $response")
                    }
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

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener {


                //Get the nearby stops from the user location
                if(it == null) {
                    Toast.makeText(this, "Location permission not granted", Toast.LENGTH_LONG).show()
                }else {
                    // Set the location of the user
                    userLocation = it
                    queryNearByStops(it.latitude, it.longitude)

                }

            }
            .addOnFailureListener{
                //Send a message to the user
                Toast.makeText(this, R.string.unable_to_get_user_location, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error: ${it.localizedMessage}")
            }
    }

    private fun showItems(listOfStops: List<StopLocation>) {
        // Stop the progress
        progressBar?.visibility = View.GONE

        // Initialize the adapter
        val recyclerViewAdapter = StopsRecyclerViewAdapter(listOfStops, userLocation, this){
            val intent = Intent(this, DepartureActivity::class.java)
            intent.putExtra(DepartureActivity.KEY_STOP, it)
            startActivity(intent)
        }

        // Connect the adapter to the recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.mainActivityRecyclerView)
        recyclerView?.apply {
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
            visibility = View.VISIBLE
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val MY_LOCATION_PERMISSION_CODE = 1000
    }


}
