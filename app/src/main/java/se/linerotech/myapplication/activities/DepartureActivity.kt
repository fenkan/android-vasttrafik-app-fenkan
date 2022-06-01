package se.linerotech.myapplication.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.linerotech.myapplication.R
import se.linerotech.myapplication.adapters.DepartureRecyclerViewAdapter
import se.linerotech.myapplication.models.BusData
import se.linerotech.myapplication.models.Departure
import se.linerotech.myapplication.models.StopLocation
import se.linerotech.myapplication.repository.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*

class DepartureActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departure)

        // Get the selected stop
        val stop = intent.getParcelableExtra<StopLocation>(KEY_STOP)
        stop?.let {

            // Initialize variables
            progressBar = findViewById(R.id.activityDepartureProgressBar)

            // Set the support action bar
            supportActionBar?.apply {
                title = it.name
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }

            // Get departure data
            queryBusesForStop(it.id.toLong())


        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun queryBusesForStop(stopId: Long) {
        RetrofitClient
            .instance
            .getDepartureTimeFromStops(stopId = stopId, date = getDate(), time = getTime())
            .enqueue(object : Callback<BusData> {
                override fun onFailure(call: Call<BusData>, t: Throwable) {
                    Toast.makeText(this@DepartureActivity, R.string.unable_to_get_departure_data, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "Error: ${t.localizedMessage}")
                }

                override fun onResponse(call: Call<BusData>, response: Response<BusData>) {
                    if (response.isSuccessful) {
                        // Show the list of departures
                            val listOfDepartures = response.body()?.departureBoard?.departure ?: emptyList()
                        showItems(listOfDepartures)
                    }else {
                        // Get a message
                        val message = when(response.code()){
                            401 -> R.string.update_api_key
                            500 -> R.string.internal_server_error
                            else -> R.string.unable_to_get_departure_data
                        }
                        // Show message to the user
                        Toast.makeText(this@DepartureActivity, message, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(Date())
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm")
        return timeFormat.format(Date())
    }

    private fun showItems(listOfDepartures: List<Departure>) {
        // Hide the progressbar
        progressBar?.visibility = View.GONE
        // Initialize the adapter
        val recyclerViewAdapter = DepartureRecyclerViewAdapter(listOfDepartures, this)
        // Configure the recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.activityDepartureRecyclerView)
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            visibility = View.VISIBLE
        }
    }

    companion object {
        const val KEY_STOP = "keyStop"
        private val TAG = DepartureActivity::class.java.simpleName
    }
}