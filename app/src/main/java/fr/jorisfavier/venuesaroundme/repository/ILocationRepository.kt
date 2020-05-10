package fr.jorisfavier.venuesaroundme.repository

import android.location.Location

interface ILocationRepository {
    /**
     * Returns the user's best most recent location currently available.
     * If a location is not available, which should happen very rarely, null will be returned
     * If the location permission has not been granted an exception will be thrown
     *
     * @return
     */
    suspend fun getCurrentUserLocation(): Location?
}
