package fr.jorisfavier.venuesaroundme.ui.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import fr.jorisfavier.venuesaroundme.R

class VenueDetailFragment : Fragment() {

    companion object {
        fun newInstance() = VenueDetailFragment()
    }

    private lateinit var viewModel: VenueDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.venue_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(VenueDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
