package fr.jorisfavier.venuesaroundme.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jorisfavier.venuesaroundme.data.ILocationRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class RestaurantsMapsViewModel(private val locationRepository: ILocationRepository) : ViewModel() {

    sealed class State {
        object FineLocationNotGranted : State()
        class Ready(val location: Location?) : State()
        class Error(val throwable: Throwable) : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _state.value =
            State.Error(
                throwable
            )
    }


    fun setFineLocationGranted(granted: Boolean) {
        if (!granted) {
            _state.value =
                State.FineLocationNotGranted
        } else {
            viewModelScope.launch(exceptionHandler) {
                _state.value =
                    State.Ready(
                        locationRepository.getCurrentUserLocation()
                    )
            }
        }
    }

}
