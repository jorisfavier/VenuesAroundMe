package fr.jorisfavier.venuesaroundme.repository

import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.model.Venue
import kotlinx.coroutines.flow.Flow

interface IVenueRepository {

    /**
     * Returns all the restaurants around a given location within the given radius
     * We will first search venues from the cache then from the api
     * The emitted list can be empty if no venues has been found
     *
     * @param location Latitude and longitude where to search for restaurants
     * @param radius Limit results to venues within this many meters of the specified location
     * @return a flow of list of venues
     */
    suspend fun getRestaurantsAroundLocation(location: LatLng, radius: Double): Flow<List<Venue>>
}
