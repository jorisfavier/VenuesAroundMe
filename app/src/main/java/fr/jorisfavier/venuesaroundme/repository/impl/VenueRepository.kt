package fr.jorisfavier.venuesaroundme.repository.impl

import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.cache.IVenueDataSource
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VenueRepository(
    private val venueService: VenueService,
    private val venueDataSource: IVenueDataSource
) : IVenueRepository {

    override suspend fun getRestaurantsAroundLocation(
        location: LatLng,
        radius: Double
    ): Flow<List<Venue>> {
        return flow {
            //First we emit the venues from the cache
            emit(venueDataSource.searchVenues(location, radius.toInt()))
            //Then we search for venues using the api
            val responseWrapper = venueService.searchVenues(
                "${location.latitude},${location.longitude}",
                radius.toInt(),
                VenueService.foodCategory
            )
            check(responseWrapper.meta.code == 200) {
                "The api should return an HTTP 200 code"
            }
            emit(responseWrapper.response.venues)
            //We insert the venues found from the api to the cache
            venueDataSource.addVenues(responseWrapper.response.venues)
        }
    }

}
