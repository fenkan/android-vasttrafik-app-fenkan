package se.linerotech.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import se.linerotech.myapplication.R
import se.linerotech.myapplication.models.StopLocation
import java.lang.StringBuilder
import java.util.*

class MapActivity : AppCompatActivity() {

    private var mapView: MapView? = null
    private var mapStyle: Style? = null
    private var mapboxMap: MapboxMap? = null
    private var stopCardView: View? = null
    private var focusedStop: StopLocation? = null
    private val listOfGbgStops: List<StopLocation> by lazy {
        val inputStream = resources.openRawResource(R.raw.gbg_stops)
        try {
            val scanner = Scanner(inputStream)
            val stringBuilder = StringBuilder()
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine())
            }
            Gson().fromJson(stringBuilder.toString(), Array<StopLocation>::class.java).asList()
        }catch(exception: Exception) {
            emptyList<StopLocation>()
        }finally {
            inputStream.close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_acces_token))

        setContentView(R.layout.activity_map)

        stopCardView = findViewById(R.id.layoutStopInfo)
        stopCardView?.setOnClickListener {
            // Create an intent
            val intent = Intent(this, DepartureActivity::class.java)
            intent.putExtra(DepartureActivity.KEY_STOP, focusedStop)
            startActivity(intent)
        }
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.MAPBOX_STREETS){
                // Map is setup and ready to be configured
                mapStyle = it
                it.addImage(ICON_ID, BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default))
            }

            // Configure the stops Icons
            configureStopIcons()

            // Show the user location
            configureUserLocation()
        }
    }

    private fun configureStopIcons() {
        mapStyle?.let {
            val symbolList = ArrayList<SymbolOptions>()
            listOfGbgStops.forEach { stop ->
                val symbolOption = SymbolOptions()
                    .withLatLng(LatLng(stop.lat.toDouble(), stop.lon.toDouble()))
                    .withIconImage(ICON_ID)
                symbolList.add(symbolOption)
            }

            // Symbol Manager
            val symbolManager = SymbolManager(mapView!!, mapboxMap!!, it).apply {
                iconAllowOverlap = true
                iconIgnorePlacement = true
                create(symbolList)
            }

            // Configure symbol click listener
            symbolManager.addClickListener { symbol ->

                // Move the camera to the markers position
                setCameraPosition(symbol.latLng.latitude, symbol.latLng.longitude, true)

                // Get information of selected stop
                val stop = listOfGbgStops.first { stop ->
                    stop.lat.toDouble() == symbol.latLng.latitude && stop.lon.toDouble() == symbol.latLng.longitude
                }

                // Save the selected stop
                focusedStop = stop

                // Show the stops information
                showStopInfo(stop)



                true

            }


        }
    }

    private fun configureUserLocation() {
        if( !PermissionsManager.areLocationPermissionsGranted(this)) {
            // Notify the user
            Toast.makeText(this, R.string.please_enable_location, Toast.LENGTH_LONG).show()

            // Focus the camera/map to GBG centrum
            setCameraPosition(GBG_CENTER.latitude, GBG_CENTER.longitude)


        }else {
            // Show the users location
            enableLocationComponent()

        }
    }

    private fun setCameraPosition(lat: Double, lng: Double, withEase: Boolean = false) {
        val position = CameraPosition.Builder()
            .target(LatLng(lat, lng))
            .zoom(14.0)
            .tilt(20.0)
            .build()

        if(withEase) {
            mapboxMap?.easeCamera(CameraUpdateFactory.newCameraPosition(position))
        } else {
            mapboxMap?.cameraPosition = position
        }


    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent() {
        if(mapStyle != null && mapboxMap != null) {
            // Basic pulsing
            val customLocationComponentOption = LocationComponentOptions.builder(this)
                .pulseEnabled(true)
                .build()

            // Instance of a location component
            val locationComponent = mapboxMap!!.locationComponent

            // Activate the custom options
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, mapStyle!!)
                    .locationComponentOptions(customLocationComponentOption)
                    .build()
            )

            // Enable the component as visible
            locationComponent.isLocationComponentEnabled = true

            // Set the camera mode
            locationComponent.cameraMode = CameraMode.TRACKING

            // Set the render mode
            locationComponent.renderMode = RenderMode.NORMAL

            // Set the camera position
            locationComponent.locationEngine?.getLastLocation(object: LocationEngineCallback<LocationEngineResult>{

                override fun onSuccess(result: LocationEngineResult?) {
                    result?.lastLocation?.let {
                        setCameraPosition(it.latitude, it.longitude)
                    }
                }

                override fun onFailure(exception: java.lang.Exception) {
                    Toast.makeText(this@MapActivity,
                        R.string.unable_to_get_user_location,
                        Toast.LENGTH_LONG).show()
                    setCameraPosition(GBG_CENTER.latitude, GBG_CENTER.longitude)
                }
            })
        }
    }

    private fun showStopInfo(stop: StopLocation) {
        // Set the visibility of the card as visible
        stopCardView?.visibility = View.VISIBLE

        // Set the name of the stop
        findViewById<TextView>(R.id.recyclerViewTextViewStopName)?.text = stop.name

        // Set the distance from the user
        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            mapboxMap?.locationComponent?.lastKnownLocation?.let {
                val distanceToStop = LatLng(it.latitude, it.longitude).distanceTo(LatLng(stop.lat.toDouble(), stop.lon.toDouble()))
                findViewById<TextView>(R.id.recyclerViewTextViewStopDistance)?.text = getString(R.string.meters_away, distanceToStop.toInt())
            }
        } else {
            findViewById<TextView>(R.id.recyclerViewTextViewStopDistance)?.visibility = View.GONE
        }

    }

    //region Lifecycle Methods
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
    //endregion

    companion object {
        private const val ICON_ID = "stop"
        private val GBG_CENTER = LatLng(57.71595454660341, 11.972795514422153)
    }

}