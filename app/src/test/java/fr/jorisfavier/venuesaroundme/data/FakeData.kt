package fr.jorisfavier.venuesaroundme.data

import com.google.android.gms.maps.model.LatLng
import fr.jorisfavier.venuesaroundme.api.model.Location
import fr.jorisfavier.venuesaroundme.api.model.VenueDetail

val fakeLatlng = LatLng(0.0, 0.0)
val fakeLocation = Location(
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

val fakeVenueDetail = VenueDetail(
    "1",
    "test",
    null,
    fakeLocation,
    5.0,
    null,
    null,
    null
)
