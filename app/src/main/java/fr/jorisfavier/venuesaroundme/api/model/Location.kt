package fr.jorisfavier.venuesaroundme.api.model

data class Location(
    val address: String,
    val cc: String,
    val city: String,
    val country: String,
    val crossStreet: String,
    val distance: Int,
    val formattedAddress: List<String>,
    val lat: Double,
    val lng: Double,
    val postalCode: String,
    val state: String
) {
    fun distanceTo(destLat: Double, destLng: Double): Double {
        val result = FloatArray(1)
        android.location.Location.distanceBetween(lat, lng, destLat, destLng, result)
        return result[0].toDouble()
    }
}
