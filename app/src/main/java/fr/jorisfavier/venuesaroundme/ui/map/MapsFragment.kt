package fr.jorisfavier.venuesaroundme.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.jorisfavier.venuesaroundme.R
import fr.jorisfavier.venuesaroundme.ui.MainActivityViewModel
import fr.jorisfavier.venuesaroundme.util.getRadius

class MapsFragment : Fragment() {

    companion object {
        const val FINE_LOCATION_PERMISSION_ID = 54
        const val DEFAULT_ZOOM_LEVEL = 15f
    }

    private lateinit var map: GoogleMap

    private val viewModel: MapsViewModel by viewModels {
        MapsViewModelFactory(
            LocationServices.getFusedLocationProviderClient(requireActivity())
        )
    }

    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private val isFineLocationGranted: Boolean
        get() =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map = it }
        initObservers()
        viewModel.setFineLocationGranted(isFineLocationGranted)
    }

    private fun initObservers() {
        sharedViewModel.fineLocationGranted.observe(viewLifecycleOwner, Observer { granted ->
            viewModel.setFineLocationGranted(granted)
        })

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                MapsViewModel.State.FineLocationNotGranted -> requestFineLocationPermission()
                is MapsViewModel.State.Ready -> initMap(state.location)
                MapsViewModel.State.LocationError -> displayLocationError()
                MapsViewModel.State.SearchError -> displayVenueSearchError()
            }
        })

        viewModel.restaurants.observe(viewLifecycleOwner, Observer { venues ->
            venues.forEach { venue ->
                map.addMarker(
                    MarkerOptions()
                        .position(LatLng(venue.location.lat, venue.location.lng))
                        .title(venue.name)
                        .snippet(venue.categories.joinToString(" - ") { it.name })
                )
            }
        })
    }

    /**
     * If the fine location has not been granted,
     * we will first display a warning message explaining why we want it,
     * then a native dialog will be prompt to the user asking for the permission
     */
    private fun requestFineLocationPermission() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.we_need_fine_location_permission)
            .setNegativeButton(android.R.string.cancel) { _, _ -> requireActivity().finish() }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (!isFineLocationGranted) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        FINE_LOCATION_PERMISSION_ID
                    )
                }
            }
            .show()
    }

    /**
     * Displays the "my location" layer and the related control on the map
     * Sets the map's camera position to the current location of the device
     * Adds a listener to the map in order to load restaurants when the user pans the map.
     *
     * @param usersLocation the current location where to moved the camera to
     */
    private fun initMap(usersLocation: Location) {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(usersLocation.latitude, usersLocation.longitude),
                DEFAULT_ZOOM_LEVEL
            )
        )
        map.setOnCameraIdleListener {
            map.clear()
            viewModel.searchNearByRestaurants(map.cameraPosition.target, map.getRadius())
        }
    }

    /**
     * Displays a location error alert dialog to the user
     *
     */
    private fun displayLocationError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.oups)
            .setMessage(R.string.location_error)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    /**
     * Displays a search error alert dialog to the user
     *
     */
    private fun displayVenueSearchError() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.oups)
            .setMessage(R.string.search_venues_error)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
