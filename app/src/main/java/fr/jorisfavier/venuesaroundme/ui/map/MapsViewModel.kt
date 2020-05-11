package fr.jorisfavier.venuesaroundme.ui.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.repository.ILocationRepository
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository
import fr.jorisfavier.venuesaroundme.util.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapsViewModel(
    private val locationRepository: ILocationRepository,
    private val venueRepository: IVenueRepository
) : ViewModel() {

    sealed class State {
        object FineLocationNotGranted : State()
        class Ready(val location: Location) : State()
        object LocationError : State()
        object SearchError : State()
    }

    private val _state = MutableLiveData<Event<State>>()
    val state: LiveData<Event<State>> = _state

    private val _restaurants = MutableLiveData<List<Venue>>()
    val restaurants: LiveData<List<Venue>> = _restaurants

    private val locationExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.w(this::class.java.simpleName, throwable)
        _state.value = Event(State.LocationError)
    }

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.w(this::class.java.simpleName, throwable)
        _state.value = Event(State.SearchError)
    }

    /**
     * Indicates if the fine location permission has been granted by the user or not
     *
     * @param granted
     */
    fun setFineLocationGranted(granted: Boolean) {
        if (_state.value?.peekContent() !is State.Ready) {
            if (!granted) {
                _state.value = Event(State.FineLocationNotGranted)
            } else {
                viewModelScope.launch(locationExceptionHandler) {
                    val currentLocation = locationRepository.getCurrentUserLocation()
                    currentLocation?.let {
                        _state.value = Event(State.Ready(it))
                    } ?: run {
                        _state.value = Event(State.LocationError)
                    }
                }
            }
        }
    }

    /**
     * Search restaurants around a given location within the given radius
     * Uses the restaurants liveData to set the results
     *
     * @param location Latitude and longitude where to search for restaurants
     * @param radius Limit results to restaurants within this many meters of the specified location
     */
    fun searchNearByRestaurants(location: LatLng, radius: Double) {
        viewModelScope.launch(searchExceptionHandler) {
            venueRepository.getRestaurantsAroundLocation(location, radius).collect {
                _restaurants.value = it
            }
        }
    }

}
