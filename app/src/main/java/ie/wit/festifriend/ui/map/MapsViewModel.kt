package ie.wit.festifriend.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import android.location.Location

class MapsViewModel : ViewModel() {
    lateinit var map: GoogleMap
    var currentLocation = MutableLiveData<Location>()
}