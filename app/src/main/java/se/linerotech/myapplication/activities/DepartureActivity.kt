package se.linerotech.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import se.linerotech.myapplication.R
import se.linerotech.myapplication.models.StopLocation

class DepartureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_departure)

        // Get the selected stop
        val stop = intent.getParcelableExtra<StopLocation>(KEY_STOP)
        stop?.let {
            supportActionBar?.apply {
                title = it.name
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }
        }

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val KEY_STOP = "keyStop"
    }
}