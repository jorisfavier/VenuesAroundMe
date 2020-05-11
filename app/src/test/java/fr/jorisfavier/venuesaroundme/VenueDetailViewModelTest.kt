package fr.jorisfavier.venuesaroundme

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.jorisfavier.venuesaroundme.data.fakeVenueDetail
import fr.jorisfavier.venuesaroundme.repository.IVenueRepository
import fr.jorisfavier.venuesaroundme.ui.detail.VenueDetailViewModel
import fr.jorisfavier.venuesaroundme.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class VenueDetailViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Test
    fun `loadDetail should load the venue detail`() {
        //given
        val venueRepo = mock<IVenueRepository> {
            onBlocking { getVenueDetail(any()) } doReturn fakeVenueDetail
        }
        val viewModel = VenueDetailViewModel(venueRepo)
        mainCoroutineRule.pauseDispatcher()

        //when
        viewModel.loadDetail("1")

        //then
        assert(
            viewModel.state.getOrAwaitValue().peekContent() == VenueDetailViewModel.State.Loading
        )
        mainCoroutineRule.resumeDispatcher()
        assert(viewModel.state.getOrAwaitValue().peekContent() == VenueDetailViewModel.State.Loaded)
        assert(viewModel.venue.getOrAwaitValue() == fakeVenueDetail)
    }

    @Test
    fun `loadDetail with an exception should emit error state`() = runBlockingTest {
        //given

        val venueRepo = mock<IVenueRepository> {
            doAnswer { throw Exception() }.`when`(mock).getVenueDetail(any())
        }
        val viewModel = VenueDetailViewModel(venueRepo)
        mainCoroutineRule.pauseDispatcher()

        //when
        viewModel.loadDetail("1")

        //then
        assert(
            viewModel.state.getOrAwaitValue().peekContent() == VenueDetailViewModel.State.Loading
        )
        mainCoroutineRule.resumeDispatcher()
        assert(viewModel.state.getOrAwaitValue().peekContent() == VenueDetailViewModel.State.Error)
    }
}
