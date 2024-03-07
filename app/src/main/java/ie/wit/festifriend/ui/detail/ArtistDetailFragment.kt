package ie.wit.festifriend.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import ie.wit.festifriend.R
import ie.wit.festifriend.databinding.FragmentArtistDetailBinding
import ie.wit.festifriend.models.ArtistModel

class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ArtistDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ArtistDetailViewModel::class.java)

        val artist = ArtistDetailFragmentArgs.fromBundle(requireArguments()).artist
        artist?.let {
            viewModel.setArtist(it)
        }

        viewModel.artist.observe(viewLifecycleOwner) { artist ->
            displayArtistDetails(artist)
        }

        return binding.root
    }

    private fun displayArtistDetails(artist: ArtistModel) {
        binding.artistName.text = artist.name
        binding.artistBio.text = artist.bio

        if (!artist.imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(artist.imageUrl)
                .into(binding.artistImage)
        } else {
            Picasso.get()
                .load(R.drawable.default_image)
                .into(binding.artistImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
