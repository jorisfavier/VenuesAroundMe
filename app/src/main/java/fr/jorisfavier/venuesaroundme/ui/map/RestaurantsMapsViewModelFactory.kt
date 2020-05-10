package fr.jorisfavier.venuesaroundme.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.cache.impl.VenueDataSource
import fr.jorisfavier.venuesaroundme.repository.impl.LocationRepository
import fr.jorisfavier.venuesaroundme.repository.impl.VenueRepository

class RestaurantsMapsViewModelFactory(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantsMapsViewModel::class.java)) {
            return RestaurantsMapsViewModel(
                LocationRepository(fusedLocationProviderClient),
                VenueRepository(VenueService.create(), VenueDataSource())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
