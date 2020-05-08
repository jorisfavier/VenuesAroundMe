package fr.jorisfavier.venuesaroundme.data.impl

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import fr.jorisfavier.venuesaroundme.data.ILocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LocationRepository(private val fusedLocationProviderClient: FusedLocationProviderClient): ILocationRepository {
    override suspend fun getCurrentUserLocation(): Location? {
        return withContext(Dispatchers.Default){
            fusedLocationProviderClient.lastLocation.await()
        }
    }
}
