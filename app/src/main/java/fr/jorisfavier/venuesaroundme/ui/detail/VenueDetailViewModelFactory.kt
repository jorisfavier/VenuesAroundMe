package fr.jorisfavier.venuesaroundme.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.cache.impl.VenueDataSource
import fr.jorisfavier.venuesaroundme.repository.impl.VenueRepository

class VenueDetailViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VenueDetailViewModel::class.java)) {
            return VenueDetailViewModel(
                VenueRepository(VenueService.create(), VenueDataSource())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

