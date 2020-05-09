package fr.jorisfavier.venuesaroundme.data

import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.model.Venue

interface IVenueRepository {

    /**
     * Returns all the restaurants around a given location within the given radius
     *
     * @param location Latitude and longitude where to search for restaurants
     * @param radius Limit results to venues within this many meters of the specified location
     * @return a list of venues or an empty list if nothing is found
     */
    suspend fun getRestaurantsAroundLocation(location: LatLng, radius: Double): List<Venue>
}
