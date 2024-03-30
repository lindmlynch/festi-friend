package ie.wit.festifriend.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.festifriend.models.PerformanceModel
import ie.wit.festifriend.models.VenueModel
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.*
import timber.log.Timber

@SuppressLint("MissingPermission")
class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseDatabase.getInstance()
    private val venuesLiveData = MutableLiveData<List<VenueModel>>()
    private val performancesLiveData = MutableLiveData<List<PerformanceModel>>()

    lateinit var map : GoogleMap
    var currentLocation = MutableLiveData<Location>()
    var locationClient : FusedLocationProviderClient
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
        .setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(5000)
        .setMaxUpdateDelayMillis(15000)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            currentLocation.value = locationResult.locations.last()
        }
    }

    init {
        getVenue()
        getPerformance()
        locationClient = LocationServices.getFusedLocationProviderClient(application)
        locationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.getMainLooper())
    }

    fun updateCurrentLocation() {
        if(locationClient.lastLocation.isSuccessful)
            locationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation.value = location!!
                    Timber.i("MAP VM LOC SUCCESS: %s", currentLocation.value)
                }
        else
            currentLocation.value = Location("Default").apply {
                latitude = 52.14113
                longitude = -10.26704
            }
        Timber.i("MAP VM LOC : %s", currentLocation.value)
    }

    private fun getVenue() {
        db.getReference("venues").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(venuesSnapshot: DataSnapshot) {
                val venueList = mutableListOf<VenueModel>()
                for (venueSnapshot in venuesSnapshot.children) {
                    val venue = venueSnapshot.getValue(VenueModel::class.java)
                    venue?.let { venueList.add(it) }
                }
                venuesLiveData.value = venueList
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getPerformance() {
        db.getReference("performances").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(performancesSnapshot: DataSnapshot) {
                val performanceList = mutableListOf<PerformanceModel>()
                for (daySnapshot in performancesSnapshot.children) {
                    for (performanceSnapshot in daySnapshot.children) {
                        val performance = performanceSnapshot.getValue(PerformanceModel::class.java)
                        performance?.let { performanceList.add(it) }
                    }
                }
                performancesLiveData.value = performanceList
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun getPerformances(venueName: String): LiveData<List<PerformanceModel>> {
        val result = MutableLiveData<List<PerformanceModel>>()

        performancesLiveData.observeForever { performances ->
            result.value = performances.filter { it.location == venueName }
        }
        return result
    }

    fun getVenues(): LiveData<List<VenueModel>> = venuesLiveData

}
