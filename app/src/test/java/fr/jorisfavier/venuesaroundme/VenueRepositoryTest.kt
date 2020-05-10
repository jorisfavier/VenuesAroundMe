package fr.jorisfavier.venuesaroundme

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.jorisfavier.venuesaroundme.api.VenueService
import fr.jorisfavier.venuesaroundme.api.model.Meta
import fr.jorisfavier.venuesaroundme.api.model.ResponseWrapper
import fr.jorisfavier.venuesaroundme.api.model.VenuesSearchResult
import fr.jorisfavier.venuesaroundme.repository.impl.VenueRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class VenueRepositoryTest {
    private val fakeLatlng = LatLng(0.0, 0.0)
    private val fakeSuccessResponse =
        ResponseWrapper(Meta(200, "1"), VenuesSearchResult(listOf()))
    private val fakeFailResponse =
        ResponseWrapper(Meta(400, "1"), VenuesSearchResult(listOf()))


    @Test
    fun `correct request should return a venue list`() {
        //given
        val venueService: VenueService = mock {
            onBlocking { searchVenues(any(), any(), any()) } doReturn fakeSuccessResponse
        }
        val venueRepo = VenueRepository(venueService)

        //when
        val actual = runBlocking { venueRepo.getRestaurantsAroundLocation(fakeLatlng, 10.0) }

        //then
        assert(fakeSuccessResponse.response.venues == actual)

    }

    @Test(expected = IllegalStateException::class)
    fun `meta code different than 200 should throw an exception`() {
        //given
        val venueService: VenueService = mock {
            onBlocking { searchVenues(any(), any(), any()) } doReturn fakeFailResponse
        }
        val venueRepo = VenueRepository(venueService)

        //when
        runBlocking { venueRepo.getRestaurantsAroundLocation(fakeLatlng, 10.0) }

        //then
        Assert.fail()
    }


}
