package fr.jorisfavier.venuesaroundme

import android.content.Context
import com.google.android.gms.location.LocationServices
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.cache.IVenueDataSource
import fr.jorisfavier.venuesaroundme.cache.impl.VenueDataSource
import fr.jorisfavier.venuesaroundme.repository.ILocationRepository
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository
import fr.jorisfavier.venuesaroundme.repository.impl.LocationRepository
import fr.jorisfavier.venuesaroundme.repository.impl.VenueRepository

interface IServiceLocator {
    val locationRepository: ILocationRepository
    val venueRepository: IVenueRepository
}

class ServiceLocator(val appContext: Context) : IServiceLocator {

    override val locationRepository: ILocationRepository by lazy {
        LocationRepository(
            LocationServices.getFusedLocationProviderClient(appContext)
        )
    }
    private val venueService: VenueService by lazy { VenueService.create() }
    private val venueDataSource: IVenueDataSource by lazy { VenueDataSource() }
    override val venueRepository: IVenueRepository by lazy {
        VenueRepository(
            venueService,
            venueDataSource
        )
    }
}
