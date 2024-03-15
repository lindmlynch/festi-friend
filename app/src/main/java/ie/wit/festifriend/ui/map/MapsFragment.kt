package ie.wit.festifriend.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.festifriend.R
import ie.wit.festifriend.models.VenueModel
import ie.wit.festifriend.models.PerformanceModel

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapsViewModel: MapsViewModel
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapsViewModel = ViewModelProvider(this)[MapsViewModel::class.java]
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupMap()
        observeVenuesAndPerformances()
    }

    private fun setupMap() {
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(52.1409, -10.2671), 14f))
        }
    }

    private fun observeVenuesAndPerformances() {
        mapsViewModel.getVenues().observe(viewLifecycleOwner) { venues ->
            mapsViewModel.getPerformances().observe(viewLifecycleOwner) { performances ->
                val performancesByVenue = performances.groupBy { it.location }

                venues.forEach { venue ->
                    val loc = LatLng(venue.latitude, venue.longitude)
                    val performerNames = performancesByVenue[venue.name]?.joinToString(", ") { it.name ?: "Unknown" }
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(loc)
                            .title(venue.name)
                            .snippet(performerNames)
                    )
                }
            }
        }
    }
}