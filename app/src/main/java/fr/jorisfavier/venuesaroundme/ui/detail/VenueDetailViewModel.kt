package fr.jorisfavier.venuesaroundme.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jorisfavier.venuesaroundme.api.model.VenueDetail
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository
import fr.jorisfavier.venuesaroundme.util.Event
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class VenueDetailViewModel(private val venueRepository: IVenueRepository) : ViewModel() {
    sealed class State {
        object Loading : State()
        object Loaded : State()
        object Error : State()
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.w(this::class.java.simpleName, throwable)
        _state.value = Event(State.Error)
    }

    private val _state = MutableLiveData<Event<State>>()
    val state: LiveData<Event<State>> = _state

    private val _venue: MutableLiveData<VenueDetail> = MutableLiveData()
    val venue: LiveData<VenueDetail> = _venue

    /**
     * Load the details information about a given venue
     *
     * @param id the venue identifier
     */
    fun loadDetail(id: String) {
        viewModelScope.launch(exceptionHandler) {
            _state.value = Event(State.Loading)
            _venue.value = venueRepository.getVenueDetail(id)
            _state.value = Event(State.Loaded)
        }
    }


}
