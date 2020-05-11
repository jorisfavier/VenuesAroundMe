package fr.jorisfavier.venuesaroundme.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import fr.jorisfavier.venuesaroundme.R
import fr.jorisfavier.venuesaroundme.ui.map.MapsFragment.Companion.FINE_LOCATION_PERMISSION_ID

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        viewModel.setFineLocationGranted(
            requestCode == FINE_LOCATION_PERMISSION_ID && grantResults.isNotEmpty()
                    && grantResults.first() == PackageManager.PERMISSION_GRANTED
        )
    }

}
