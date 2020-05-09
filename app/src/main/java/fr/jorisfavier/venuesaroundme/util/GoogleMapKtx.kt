package fr.jorisfavier.venuesaroundme.util

import android.location.Location
import com.google.android.gms.maps.GoogleMap

/**
 * Indicates the distance in meters between the center
 * and the farthest point of the current viewport
 *
 * @return the distance in meters
 */
fun GoogleMap.getRadius(): Double {
    val center = this.projection.visibleRegion.latLngBounds.center
    val ne = this.projection.visibleRegion.latLngBounds.northeast
    val result = FloatArray(1)

    Location.distanceBetween(center.latitude, center.longitude, ne.latitude, ne.longitude, result)
    return result[0].toDouble()
}
