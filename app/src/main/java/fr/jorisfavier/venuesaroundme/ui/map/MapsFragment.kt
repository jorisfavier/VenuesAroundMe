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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.jorisfavier.venuesaroundme.R
import fr.jorisfavier.venuesaroundme.ui.MainActivityViewModel
import fr.jorisfavier.venuesaroundme.util.findNavController
import fr.jorisfavier.venuesaroundme.util.getRadius
import fr.jorisfavier.venuesaroundme.util.getServiceLocator

class MapsFragment : Fragment() {

    companion object {
        const val FINE_LOCATION_PERMISSION_ID = 54
        const val DEFAULT_ZOOM_LEVEL = 15f
    }

    private lateinit var map: GoogleMap

    private val viewModel: MapsViewModel by viewModels {
        MapsViewModelFactory(
            getServiceLocator().locationRepository,
            getServiceLocator().venueRepository
        )
    }

    private var shouldReloadVenues = true

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
        mapFragment.getMapAsync {
            map = it
            initObservers()
            viewModel.setFineLocationGranted(isFineLocationGranted)
            if (savedInstanceState != null) {
                initMap()
            }
        }
    }

    private fun initObservers() {
        sharedViewModel.fineLocationGranted.observe(viewLifecycleOwner, Observer { granted ->
            viewModel.setFineLocationGranted(granted)
        })

        viewModel.state.observe(viewLifecycleOwner, Observer { stateEvent ->
            stateEvent.getContentIfNotHandled()?.let { state ->
                when (state) {
                    MapsViewModel.State.FineLocationNotGranted -> requestFineLocationPermission()
                    is MapsViewModel.State.Ready -> initMap(state.location)
                    MapsViewModel.State.LocationError -> displayLocationError()
                    MapsViewModel.State.SearchError -> displayVenueSearchError()
                }
            }
        })

        viewModel.restaurants.observe(viewLifecycleOwner, Observer { venues ->
            venues.forEach { venue ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(venue.location.lat, venue.location.lng))
                        .title(venue.name)
                        .snippet(venue.categories.joinToString(" - ") { it.name })
                )
                marker.tag = venue.id
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
     * Sets the map's camera position to the given location
     * Adds a listener to the map in order to load restaurants when the user pans the map.
     * Sets a click listener on the infoWindow in order to open the venue detail
     *
     * @param usersLocation the current location where to moved the camera to, if none is provided
     * the camera will not be moved
     */
    private fun initMap(usersLocation: Location? = null) {
        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        usersLocation?.let {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude),
                    DEFAULT_ZOOM_LEVEL
                )
            )
        }
        map.setOnCameraIdleListener {
            if (shouldReloadVenues) {
                map.clear()
                viewModel.searchNearByRestaurants(map.cameraPosition.target, map.getRadius())
            }
            shouldReloadVenues = true
        }
        map.setOnInfoWindowClickListener {
            val action =
                MapsFragmentDirections.actionMapsFragmentToVenueDetailFragment(it.tag.toString())
            findNavController().navigate(action)
        }
        //When clicking on a marker the camera will move and idle
        //here we are preventing a reload of the venues around the marker
        map.setOnMarkerClickListener {
            shouldReloadVenues = false
            false
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
