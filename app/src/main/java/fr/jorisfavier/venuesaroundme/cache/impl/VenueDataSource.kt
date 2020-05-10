package fr.jorisfavier.venuesaroundme.cache.impl

import androidx.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.cache.IVenueDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VenueDataSource(private val maxCacheSize: Int = DEFAULT_MAX_CACHE_SIZE) : IVenueDataSource {

    companion object {
        const val DEFAULT_MAX_CACHE_SIZE = 500
    }

    //We use a Set in order to avoid duplicated elements
    //We use a LinkedHashSet in order to keep the elements ordered following the insertion order
    // because at some point we will remove the oldest ones.
    @VisibleForTesting
    val cache = LinkedHashSet<Venue>()

    override suspend fun searchVenues(location: LatLng, radius: Int): List<Venue> {
        return withContext(Dispatchers.Default) {
            cache.filter { it.location.distanceTo(location.latitude, location.longitude) <= radius }
        }
    }

    override suspend fun addVenues(venues: List<Venue>) {
        withContext(Dispatchers.Default) {
            //If we are going to exceed the cache size by inserting new elements,
            // then we need to remove the oldest element in the cache which is the ones on the first positions
            if (cache.size + venues.size > maxCacheSize) {
                var elementToRemoveCount = (cache.size + venues.size) - maxCacheSize
                cache.removeAll {
                    elementToRemoveCount-- > 0
                }
            }
            cache.addAll(venues)
        }
    }
}

