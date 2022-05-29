package se.linerotech.myapplication.adapters

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import se.linerotech.myapplication.R
import se.linerotech.myapplication.models.StopLocation
import kotlin.math.roundToInt

class StopsRecyclerViewAdapter(
    private val listOfStops: List<StopLocation>,
    private val userLocation: Location?,
    private val context: Context,
    private val clickListener: (StopLocation) -> Unit
    ) :
    RecyclerView.Adapter<StopsRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listOfStops.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Stop we want to show
        val stop = listOfStops[position]
        holder.bind(stop, userLocation, context, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

       private val textViewStopName =
           view.findViewById<TextView>(R.id.recyclerViewTextViewStopName)
        private val textViewDistanceToStop =
            view.findViewById<TextView>(R.id.recyclerViewTextViewStopDistance)
        private val rootView = view.findViewById<CardView>(R.id.recyclerViewCardView)

        fun bind(stop: StopLocation, userLocation: Location?, context: Context, clickListener: (StopLocation) -> Unit){
            // Set the name of the stop
            textViewStopName?.text = stop.name

            // Calculate the distance to the stop
            val stopLocation = Location("")
            stopLocation.latitude = stop.lat.toDouble()
            stopLocation.longitude = stop.lon.toDouble()
            val distanceToStop = userLocation?.distanceTo(stopLocation) ?: 0.0F

            // Show the distance to the stop
            textViewDistanceToStop.text = context.getString(R.string.meters_away, distanceToStop.roundToInt())

            // Set the click listener
            rootView.setOnClickListener {
                clickListener(stop)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_recylcer_view_stops, parent, false)

                return ViewHolder(view)
            }
        }
    }


}