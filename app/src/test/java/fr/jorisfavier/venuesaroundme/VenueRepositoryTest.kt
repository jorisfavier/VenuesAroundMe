package fr.jorisfavier.venuesaroundme

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.api.model.Meta
import fr.jorisfavier.venuesaroundme.api.model.ResponseWrapper
import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.api.model.VenuesSearchResult
import fr.jorisfavier.venuesaroundme.cache.IVenueDataSource
import fr.jorisfavier.venuesaroundme.repository.impl.VenueRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class VenueRepositoryTest {
    private val fakeLatlng = LatLng(0.0, 0.0)
    private val fakeSuccessResponse =
        ResponseWrapper(Meta(200, "1"), VenuesSearchResult(listOf()))
    private val fakeFailResponse =
        ResponseWrapper(Meta(400, "1"), VenuesSearchResult(listOf()))

    private val fakeLocationDTO = fr.jorisfavier.venuesaroundme.api.model.Location(
        "",
        "",
        "",
        "",
        "",
        0,
        listOf(),
        0.0,
        0.0,
        "",
        ""
    )

    private val fakeVenueListFromCache = listOf(
        Venue(listOf(), "3", fakeLocationDTO, "test", 0.0),
        Venue(listOf(), "4", fakeLocationDTO, "test 2", 0.0)
    )

    @Test
    fun `correct request should return a venue list from the cache first and then from the api`() {
        //given
        val venueService: VenueService = mock {
            onBlocking { searchVenues(any(), any(), any()) } doReturn fakeSuccessResponse
        }
        val venueDataSource = mock<IVenueDataSource> {
            onBlocking { searchVenues(any(), any()) } doReturn fakeVenueListFromCache
        }
        val venueRepo = VenueRepository(venueService, venueDataSource)

        //when
        val flow = runBlocking { venueRepo.getRestaurantsAroundLocation(fakeLatlng, 10.0) }
        var actual = mutableListOf<List<Venue>>()
        runBlocking { flow.toList(actual) }

        //then
        assert(actual.size == 2)
        assert(fakeVenueListFromCache == actual[0])
        assert(fakeSuccessResponse.response.venues == actual[1])

    }

    @Test(expected = IllegalStateException::class)
    fun `meta code different than 200 should throw an exception`() {
        //given
        val venueService: VenueService = mock {
            onBlocking { searchVenues(any(), any(), any()) } doReturn fakeFailResponse
        }
        val venueRepo = VenueRepository(venueService, mock())

        //when
        runBlocking { venueRepo.getRestaurantsAroundLocation(fakeLatlng, 10.0).collect() }

        //then
        Assert.fail()
    }


}
