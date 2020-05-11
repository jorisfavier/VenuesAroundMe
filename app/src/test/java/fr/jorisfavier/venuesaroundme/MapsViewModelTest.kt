package fr.jorisfavier.venuesaroundme

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.data.fakeLocation
import fr.jorisfavier.venuesaroundme.repository.ILocationRepository
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository
import fr.jorisfavier.venuesaroundme.ui.map.MapsViewModel
import fr.jorisfavier.venuesaroundme.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MapsViewModelTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `not granting fine location should emit FineLocationNotGranted state`() {
        //given
        val viewModel = MapsViewModel(mock(), mock())

        //when
        viewModel.setFineLocationGranted(false)

        //then
        assert(
            viewModel.state.getOrAwaitValue()
                .peekContent() == MapsViewModel.State.FineLocationNotGranted
        )
    }

    @Test
    fun `granting fine location and LocationRepository throwing an exception should emit LocationError state`() =
        runBlockingTest {
            //given
            val locationRepo = mock<ILocationRepository> {
                doAnswer { throw Exception() }.`when`(mock).getCurrentUserLocation()
            }

            val viewModel = MapsViewModel(locationRepo, mock())

            //when
            viewModel.setFineLocationGranted(true)

            //then
            assert(
                viewModel.state.getOrAwaitValue().peekContent() == MapsViewModel.State.LocationError
            )
        }

    @Test
    fun `granting fine location and LocationRepository returning a null Location should emit LocationError state`() {

        //given
        val locationRepo = mock<ILocationRepository> {
            onBlocking { getCurrentUserLocation() } doReturn null
        }

        val viewModel = MapsViewModel(locationRepo, mock())

        //when
        viewModel.setFineLocationGranted(true)

        //then
        assert(viewModel.state.getOrAwaitValue().peekContent() == MapsViewModel.State.LocationError)
    }

    @Test
    fun `granting fine location should emit Ready state`() {
        //given
        val fakeLocation = mock<Location> {
            on { latitude } doReturn 1.0
            on { longitude } doReturn 1.0
        }

        val locationRepo = mock<ILocationRepository> {
            onBlocking { getCurrentUserLocation() } doReturn fakeLocation
        }

        val viewModel = MapsViewModel(locationRepo, mock())

        //when
        viewModel.setFineLocationGranted(true)

        //then
        val state = viewModel.state.getOrAwaitValue().peekContent()
        assert(state is MapsViewModel.State.Ready && state.location == fakeLocation)

    }

    @Test
    fun `searchNearByRestaurants should emit restaurants`() {
        //given
        val fakeVenueList = listOf(
            Venue(listOf(), "1", fakeLocation, "test", 0.0),
            Venue(listOf(), "2", fakeLocation, "test 2", 0.0)
        )

        val fakeVenueFlow = flowOf(fakeVenueList)
        val venueRepo = mock<IVenueRepository> {
            onBlocking { getRestaurantsAroundLocation(any(), any()) } doReturn fakeVenueFlow
        }

        val viewModel = MapsViewModel(mock(), venueRepo)

        //when
        viewModel.searchNearByRestaurants(LatLng(0.0, 0.0), 10.0)

        //then
        assert(viewModel.restaurants.getOrAwaitValue() == fakeVenueList)
    }

    @Test
    fun `searchNearByRestaurants with VenueRepository throwing an exception should emit SearchError state`() =
        runBlockingTest {
            //given
            val venueRepo = mock<IVenueRepository> {
                doAnswer { throw Exception() }.`when`(mock)
                    .getRestaurantsAroundLocation(any(), any())
            }

            val viewModel = MapsViewModel(mock(), venueRepo)

            //when
            viewModel.searchNearByRestaurants(LatLng(0.0, 0.0), 10.0)

            //then
            assert(
                viewModel.state.getOrAwaitValue().peekContent() == MapsViewModel.State.SearchError
            )
        }
}
