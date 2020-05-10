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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RestaurantsMapsViewModel(
    private val locationRepository: ILocationRepository,
    private val venueRepository: IVenueRepository
) : ViewModel() {

    sealed class State {
        object FineLocationNotGranted : State()
        class Ready(val location: Location) : State()
        object LocationError : State()
        object SearchError : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _restaurants = MutableLiveData<List<Venue>>()
    val restaurants: LiveData<List<Venue>> = _restaurants

    private val locationExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.w(this::class.java.simpleName, throwable)
        _state.value = State.LocationError
    }

    private val searchExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.w(this::class.java.simpleName, throwable)
        _state.value = State.SearchError
    }


    fun setFineLocationGranted(granted: Boolean) {
        if (!granted) {
            _state.value = State.FineLocationNotGranted
        } else {
            viewModelScope.launch(locationExceptionHandler) {
                val currentLocation = locationRepository.getCurrentUserLocation()
                currentLocation?.let {
                    _state.value = State.Ready(it)
                } ?: run {
                    _state.value = State.LocationError
                }
            }
        }
    }

    fun searchNearByRestaurants(location: LatLng, radius: Double) {
        viewModelScope.launch(searchExceptionHandler) {
            venueRepository.getRestaurantsAroundLocation(location, radius).collect {
                _restaurants.value = it
            }
        }
    }

}
