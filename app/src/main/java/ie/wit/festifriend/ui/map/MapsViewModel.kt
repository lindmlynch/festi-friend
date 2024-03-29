package ie.wit.festifriend.ui.map


import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.festifriend.models.PerformanceModel
import ie.wit.festifriend.models.VenueModel

class MapsViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val venuesLiveData = MutableLiveData<List<VenueModel>>()
    private val performancesLiveData = MutableLiveData<List<PerformanceModel>>()

    init {
        getVenue()
        getPerformance()
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
