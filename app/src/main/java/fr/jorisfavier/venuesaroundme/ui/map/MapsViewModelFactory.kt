package fr.jorisfavier.venuesaroundme.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.jorisfavier.venuesaroundme.repository.ILocationRepository
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository

class MapsViewModelFactory(
    private val locationRepository: ILocationRepository,
    private val venueRepository: IVenueRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(locationRepository, venueRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
