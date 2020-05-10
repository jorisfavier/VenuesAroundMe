package fr.jorisfavier.venuesaroundme.api.model

data class VenueDetail(
    val id: String,
    val name: String,
    val contact: Contact?,
    val location: Location,
    val rating: Double?,
    val ratingColor: String?,
    val description: String?,
    val bestPhoto: BestPhoto?
) {
    fun getPhotoUrl() = bestPhoto?.prefix + "original" + bestPhoto?.suffix
}
