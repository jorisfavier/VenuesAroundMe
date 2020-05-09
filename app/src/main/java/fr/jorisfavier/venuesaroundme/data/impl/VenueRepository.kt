package fr.jorisfavier.venuesaroundme.data.impl

import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.data.IVenueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VenueRepository(private val venueService: VenueService) : IVenueRepository {
    override suspend fun getRestaurantsAroundLocation(
        location: LatLng,
        radius: Double
    ): List<Venue> {
        return withContext(Dispatchers.IO) {
            val responseWrapper = venueService.searchVenues(
                "${location.latitude},${location.longitude}",
                radius.toInt(),
                VenueService.foodCategory
            )
            check(responseWrapper.meta.code == 200) {
                "The api should return an HTTP 200 code"
            }
            responseWrapper.response.venues
        }
    }
}
