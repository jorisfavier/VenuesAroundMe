package fr.jorisfavier.venuesaroundme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RestaurantsMapsActivity : AppCompatActivity() {

    companion object {
        const val FINE_LOCATION_PERMISSION_ID = 54
    }

    private lateinit var map: GoogleMap

    private val viewModel: RestaurantsMapsViewModel by viewModels()

    private val isFineLocationGranted: Boolean
        get() =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map = it }
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.setFineLocationGranted(isFineLocationGranted)
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

    private fun initObservers() {
        viewModel.state.observe(this, Observer { state ->
            if (state == RestaurantsMapsViewModel.State.FineLocationNotGranted) {
                requestFineLocationPermission()
            } else {
                initMap()
            }
        })
    }

    /**
     * If the fine location has not been granted a native dialog will be prompt to the user
     * asking for the permission
     */
    private fun requestFineLocationPermission() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.we_need_fine_location_permission)
            .setNegativeButton(android.R.string.cancel) { _, _ -> finish() }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (!isFineLocationGranted) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        FINE_LOCATION_PERMISSION_ID
                    )
                }
            }
            .show()
    }

    private fun initMap() {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
    }
}
