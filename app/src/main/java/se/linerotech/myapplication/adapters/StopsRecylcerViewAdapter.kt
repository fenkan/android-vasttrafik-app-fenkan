package se.linerotech.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.linerotech.myapplication.R
import se.linerotech.myapplication.models.StopLocation

class StopsRecyclerViewAdapter(private val listOfStops: List<StopLocation>): RecyclerView.Adapter<StopsRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listOfStops.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Stop we want to show
        val stop = listOfStops[position]
        holder.bind(stop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.create(parent)


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

       private val textViewStopName =
           view.findViewById<TextView>(R.id.recyclerViewTextViewStopName)

        fun bind(stop: StopLocation){
            textViewStopName?.text = stop.name
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