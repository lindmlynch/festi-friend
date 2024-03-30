package ie.wit.festifriend.ui.map

import android.annotation.SuppressLint
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
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
import com.squareup.picasso.Picasso
import ie.wit.festifriend.R
import ie.wit.festifriend.models.VenueModel
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapsViewModel: MapsViewModel
    private var googleMap: GoogleMap? = null
    private lateinit var venueDetailCard: CardView
    private var selectedMarker: Marker? = null

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        setupMap()
        enableUserLocation()
        observeVenuesAndPerformances()
    }

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
        mapFragment?.getMapAsync(callback)
        venueDetailCard = view.findViewById(R.id.cardVenueDetails)
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
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
            venues.forEach { venue ->
                val loc = LatLng(venue.latitude, venue.longitude)
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(loc)
                        .title(venue.name)
                )

                marker?.tag = venue
            }
        }
        googleMap?.setOnMarkerClickListener { marker ->
            if (selectedMarker == marker) {

                venueDetailCard.visibility = View.GONE
                selectedMarker = null
            } else {
                showVenueDetailsCard(marker)
                selectedMarker = marker
            }
            true
        }
    }

    private fun mapClickListener() {
        googleMap?.setOnMapClickListener {
            venueDetailCard.visibility = View.GONE
            selectedMarker = null
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupMap()
        observeVenuesAndPerformances()
        mapClickListener()
    }


    private fun showVenueDetailsCard(marker: Marker) {
        val venue = marker.tag as? VenueModel

        venue?.let {
            val venueName = venueDetailCard.findViewById<TextView>(R.id.venueName)
            val venueImage = venueDetailCard.findViewById<ImageView>(R.id.venueImage)
            val performancesContainer = venueDetailCard.findViewById<LinearLayout>(R.id.performancesContainer)

            venueName.text = venue.name
            Picasso.get().load(venue.imageUrl).placeholder(R.drawable.ic_launcher_background).into(venueImage)

            performancesContainer.removeAllViews()
            mapsViewModel.getPerformances(venue.name).observe(viewLifecycleOwner) { performances ->
                performances.forEach { performance ->
                    val performanceView = LayoutInflater.from(context).inflate(R.layout.performance_item, performancesContainer, false)
                    val performanceName = performanceView.findViewById<TextView>(R.id.performanceName)
                    val performanceDay = performanceView.findViewById<TextView>(R.id.performanceDay)
                    val performanceStartTime = performanceView.findViewById<TextView>(R.id.performanceStartTime)

                    performanceName.text = performance.name
                    performanceDay.text = performance.day
                    performanceStartTime.text = performance.startTime

                    performancesContainer.addView(performanceView)
                }
            }
            venueDetailCard.visibility = View.VISIBLE
        }
    }
}
