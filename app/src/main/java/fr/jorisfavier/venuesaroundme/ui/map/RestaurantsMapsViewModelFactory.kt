package fr.jorisfavier.venuesaroundme.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import fr.jorisfavier.venuesaroundme.data.impl.LocationRepository

class RestaurantsMapsViewModelFactory(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantsMapsViewModel::class.java)) {
            return RestaurantsMapsViewModel(
                LocationRepository(fusedLocationProviderClient)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
