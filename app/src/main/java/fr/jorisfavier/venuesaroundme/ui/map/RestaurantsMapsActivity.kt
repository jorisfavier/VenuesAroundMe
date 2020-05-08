package fr.jorisfavier.venuesaroundme.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.jorisfavier.venuesaroundme.R

class RestaurantsMapsActivity : AppCompatActivity() {

    companion object {
        const val FINE_LOCATION_PERMISSION_ID = 54
        const val DEFAULT_ZOOM_LEVEL = 15f
    }

    private lateinit var map: GoogleMap

    private val viewModel: RestaurantsMapsViewModel by viewModels {
        RestaurantsMapsViewModelFactory(
            LocationServices.getFusedLocationProviderClient(this)
        )
    }

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
            when (state) {
                RestaurantsMapsViewModel.State.FineLocationNotGranted -> {
                    requestFineLocationPermission()
                }
                is RestaurantsMapsViewModel.State.Ready -> {
                    initMap(state.location)
                }
                is RestaurantsMapsViewModel.State.Error -> {
                    displayError(state.throwable)
                }
            }
        })
    }

    /**
     * If the fine location has not been granted,
     * we will first display a warning message explaining why we want it,
     * then a native dialog will be prompt to the user asking for the permission
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

    /**
     * Displays the "my location" layer and the related control on the map
     * and set the map's camera position to the current location of the device
     *
     * @param usersLocation the current location where to moved the camera to
     */
    private fun initMap(usersLocation: Location?) {
        usersLocation?.let {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        usersLocation.latitude,
                        usersLocation.longitude
                    ),
                    DEFAULT_ZOOM_LEVEL
                )
            )
        }
    }

    /**
     * Displays a generic error alert dialog to the user
     *
     * @param throwable
     */
    private fun displayError(throwable: Throwable) {
        Log.w(RestaurantsMapsActivity::class.java.simpleName, throwable)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.oups)
            .setMessage(R.string.an_error_occurred)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
