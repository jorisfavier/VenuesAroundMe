package fr.jorisfavier.venuesaroundme.util

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import fr.jorisfavier.venuesaroundme.ServiceLocator
import fr.jorisfavier.venuesaroundme.VenueApplication

fun Fragment.findNavController(): NavController =
    NavHostFragment.findNavController(this)

fun Fragment.getServiceLocator(): ServiceLocator =
    (requireContext().applicationContext as VenueApplication).serviceLocator
