package fr.jorisfavier.venuesaroundme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RestaurantsMapsViewModel : ViewModel() {

    sealed class State {
        object FineLocationNotGranted : State()
        object Ready : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state


    fun setFineLocationGranted(granted: Boolean) {
        _state.value = if (!granted) State.FineLocationNotGranted else State.Ready
    }

}
