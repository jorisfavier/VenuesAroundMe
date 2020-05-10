package fr.jorisfavier.venuesaroundme.cache

import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.model.Venue

interface IVenueDataSource {
    /**
     * Search for persisted venues which are in the given radius to the given position
     *
     * @param location
     * @param radius
     * @return an empty list if no venues has been found otherwise a list of venues
     */
    suspend fun searchVenues(location: LatLng, radius: Int): List<Venue>

    /**
     * Persists a given list of venues
     *
     * @param venues
     */
    suspend fun addVenues(venues: List<Venue>)
}
