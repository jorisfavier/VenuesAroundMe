package fr.jorisfavier.venuesaroundme

import android.app.Application

class VenueApplication : Application() {
    val serviceLocator: ServiceLocator by lazy { ServiceLocator(this) }
}
