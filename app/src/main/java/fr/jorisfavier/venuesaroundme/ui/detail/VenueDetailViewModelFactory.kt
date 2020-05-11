package fr.jorisfavier.venuesaroundme.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository

class VenueDetailViewModelFactory(private val venueRepository: IVenueRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VenueDetailViewModel::class.java)) {
            return VenueDetailViewModel(venueRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

