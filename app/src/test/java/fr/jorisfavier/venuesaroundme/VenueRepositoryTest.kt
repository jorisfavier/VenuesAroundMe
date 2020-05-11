package fr.jorisfavier.venuesaroundme

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.api.model.*
import fr.jorisfavier.venuesaroundme.cache.IVenueDataSource
import fr.jorisfavier.venuesaroundme.data.fakeLatlng
import fr.jorisfavier.venuesaroundme.data.fakeLocation
import fr.jorisfavier.venuesaroundme.data.fakeVenueDetail
import fr.jorisfavier.venuesaroundme.repository.impl.VenueRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class VenueRepositoryTest {

    @Test
    fun `getRestaurantsAroundLocation should return a venue list from the cache first and then from the api`() {
        //given
        val fakeVenueListFromCache = listOf(
            Venue(listOf(), "3", fakeLocation, "test", 0.0),
            Venue(listOf(), "4", fakeLocation, "test 2", 0.0)
        )
        val fakeSuccessResponse =
            ResponseWrapper(Meta(200, "1"), VenuesSearchResult(listOf()))
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
    fun `getRestaurantsAroundLocation with meta code different than 200 should throw an exception`() {
        //given
        val fakeFailResponse =
            ResponseWrapper(Meta(400, "1"), VenuesSearchResult(listOf()))
        val venueService: VenueService = mock {
            onBlocking { searchVenues(any(), any(), any()) } doReturn fakeFailResponse
        }
        val venueRepo = VenueRepository(venueService, mock())

        //when
        runBlocking { venueRepo.getRestaurantsAroundLocation(fakeLatlng, 10.0).collect() }

        //then
        Assert.fail()
    }

    @Test(expected = IllegalStateException::class)
    fun `getVenueDetail with meta code different than 200 should throw an exception`() {
        //given
        val fakeFailResponse =
            ResponseWrapper(Meta(400, "1"), VenueDetailResult(fakeVenueDetail))
        val venueService: VenueService = mock {
            onBlocking { getVenueDetail(any()) } doReturn fakeFailResponse
        }
        val venueRepo = VenueRepository(venueService, mock())

        //when
        runBlocking { venueRepo.getVenueDetail("1") }

        //then
        Assert.fail()
    }


}
