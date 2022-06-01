package se.linerotech.myapplication.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import se.linerotech.myapplication.R
import se.linerotech.myapplication.models.Departure

class DepartureRecyclerViewAdapter(
    private val listOfDepartures: List<Departure>,
    private val context: Context
    ): RecyclerView.Adapter<DepartureRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listOfDepartures.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfDepartures[position], context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val textViewDirection =
            view.findViewById<TextView>(R.id.recyclerViewDepartureTextViewDirection)
        private val imageViewVehicleType = view.findViewById<ImageView>(R.id.recyclerViewImageViewVehicleType)
        private val cardViewLineBackground = view.findViewById<CardView>(R.id.recyclerViewCardViewLineBackground)
        private val textViewLine = view.findViewById<TextView>(R.id.recyclerViewTextViewLineNumber)
        private val textViewDepartureTime = view.findViewById<TextView>(R.id.recyclerViewTextViewDepartureTime)
        private val textViewTrack = view.findViewById<TextView>(R.id.recyclerViewTextViewTrack)

        @SuppressWarnings("Range")
        fun bind(departure: Departure, context: Context){
            textViewDirection.text = departure.direction
            textViewDepartureTime.text = departure.time
            textViewTrack.text = context.getString(R.string.platform, departure.track)
            textViewLine.apply {
                text = departure.sname
                setTextColor(Color.parseColor(departure.bgColor))
            }
            cardViewLineBackground.setCardBackgroundColor(Color.parseColor(departure.fgColor))

            // Determine the type of vehicle
            val vehicleDrawable = when(departure.type) {
                "TRAM" -> R.drawable.icon_tram
                "BOAT" -> R.drawable.icon_boat
                else -> R.drawable.icon_bus
            }

            imageViewVehicleType.setImageDrawable(context.getDrawable(vehicleDrawable))
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_recycler_view_departure, parent, false)
                return ViewHolder(view)

            }
        }
    }
}