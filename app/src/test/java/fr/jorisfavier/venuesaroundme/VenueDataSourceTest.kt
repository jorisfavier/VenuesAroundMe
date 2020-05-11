package fr.jorisfavier.venuesaroundme

import fr.jorisfavier.venuesaroundme.api.model.Venue
import fr.jorisfavier.venuesaroundme.cache.impl.VenueDataSource
import fr.jorisfavier.venuesaroundme.data.fakeLocation
import kotlinx.coroutines.runBlocking
import org.junit.Test

class VenueDataSourceTest {

    @Test
    fun `inserting 3 distinct venues should add 3 venues to the cache`() {
        //given
        val fakeVenues = listOf(
            Venue(listOf(), "1", fakeLocation, "test", 0.0),
            Venue(listOf(), "2", fakeLocation, "test 2", 0.0),
            Venue(listOf(), "3", fakeLocation, "test 3", 0.0)
        )
        val dataSource = VenueDataSource()

        //when
        runBlocking { dataSource.addVenues(fakeVenues) }

        //then
        assert(dataSource.cache.containsAll(fakeVenues))
    }

    @Test
    fun `inserting 2 venues with the same id should add 1 venue to the cache`() {
        //given
        val fakeVenue = Venue(listOf(), "1", fakeLocation, "test", 0.0)
        val fakeVenues = listOf(fakeVenue, fakeVenue)
        val dataSource = VenueDataSource()

        //when
        runBlocking { dataSource.addVenues(fakeVenues) }

        //then
        assert(dataSource.cache.size == 1)
        assert(dataSource.cache.contains(fakeVenue))
    }

    @Test
    fun `exceeding cache size should remove the oldest elements and insert new ones`() {
        //given
        val fakeVenues1 = listOf(
            Venue(listOf(), "1", fakeLocation, "test", 0.0),
            Venue(listOf(), "2", fakeLocation, "test 2", 0.0),
            Venue(listOf(), "3", fakeLocation, "test 3", 0.0)
        )
        val fakeVenues2 = listOf(
            Venue(listOf(), "4", fakeLocation, "test", 0.0),
            Venue(listOf(), "5", fakeLocation, "test 2", 0.0),
            Venue(listOf(), "6", fakeLocation, "test 3", 0.0)
        )
        val dataSource = VenueDataSource(5)

        //when
        runBlocking { dataSource.addVenues(fakeVenues1) }
        runBlocking { dataSource.addVenues(fakeVenues2) }

        //then
        assert(dataSource.cache.size == 5)
        assert(!dataSource.cache.contains(fakeVenues1[0]))
    }
}
