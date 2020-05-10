package fr.jorisfavier.venuesaroundme.ui.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import fr.jorisfavier.venuesaroundme.R
import fr.jorisfavier.venuesaroundme.api.model.VenueDetail
import kotlinx.android.synthetic.main.venue_detail_fragment.*

class VenueDetailFragment : Fragment() {

    private val viewModel: VenueDetailViewModel by viewModels { VenueDetailViewModelFactory() }

    private val args: VenueDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.venue_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        viewModel.loadDetail(args.venueId)
    }

    private fun initObserver() {
        viewModel.state.observe(viewLifecycleOwner, Observer { stateEvent ->
            stateEvent.getContentIfNotHandled()?.let { state ->
                detailLoader.isVisible = state == VenueDetailViewModel.State.Loading
                detailError.isVisible = state is VenueDetailViewModel.State.Error
                detailContainer.isVisible = state == VenueDetailViewModel.State.Loaded
            }
        })

        viewModel.venue.observe(viewLifecycleOwner, Observer {
            bindVenueDetail(it)
        })
    }

    /**
     * Bind the venue details information to the UI
     *
     * @param venue the venue details
     */
    private fun bindVenueDetail(venue: VenueDetail) {
        Glide.with(requireContext())
            .load(venue.getPhotoUrl())
            .placeholder(ColorDrawable(Color.GRAY))
            .centerCrop()
            .into(detailImage)

        detailName.text = venue.name
        detailAddress.text = venue.location.formattedAddress.joinToString(separator = "\n")
        detailContact.isVisible = venue.contact != null
        detailContactTitle.isVisible = venue.contact != null
        detailContact.text = venue.contact?.formattedPhone
        detailRating.text = venue.rating?.toString() ?: "Unknown"
        detailDescription.text = venue.description
    }

}
