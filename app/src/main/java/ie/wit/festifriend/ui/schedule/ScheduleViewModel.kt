package ie.wit.festifriend.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import ie.wit.festifriend.models.PerformanceModel

class ScheduleViewModel : ViewModel() {

    private val dbPerformances = FirebaseDatabase.getInstance().getReference("performances")
    private val dbFavourites = FirebaseDatabase.getInstance().getReference("favourites")

    private val _allPerformances = MutableLiveData<List<PerformanceModel>>().apply { value = emptyList() }
    private val _performances = MutableLiveData<List<PerformanceModel>>()
    val performances: LiveData<List<PerformanceModel>> = _performances

    private val _favourites = MutableLiveData<Set<String>>()
    private val _showFavourites = MutableLiveData(false)
    val showFavourites: LiveData<Boolean> = _showFavourites

    private val performanceEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val performances = mutableListOf<PerformanceModel>()
            snapshot.children.forEach { daySnapshot ->
                daySnapshot.children.forEach { performanceSnapshot ->
                    val performance = performanceSnapshot.getValue(PerformanceModel::class.java)?.apply {
                        id = performanceSnapshot.key
                        day = daySnapshot.key
                    }
                    performance?.let { performances.add(it) }
                }
            }
            _allPerformances.value = performances
            updatePerformances(_favourites.value ?: emptySet())
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    private val favouritesEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val userFavourites = snapshot.children
                .filter { it.getValue(Boolean::class.java) ?: false }
                .map { it.key ?: "" }
                .toSet()
            _favourites.value = userFavourites
            updatePerformances(userFavourites)
        }
        override fun onCancelled(error: DatabaseError) { }
    }

    init {
        dbPerformances.addValueEventListener(performanceEventListener)
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            dbFavourites.child(userId).addValueEventListener(favouritesEventListener)
        }
    }

    fun setShowFavourites(showFavourites: Boolean) {
        _showFavourites.value = showFavourites
        updateFilteredPerformances()
    }

    private fun updatePerformances(favouritesSet: Set<String>) {
        _performances.value = _allPerformances.value?.map { performance ->
            performance.copy(favourite = favouritesSet.contains(performance.id))
        }
    }

    private fun updateFilteredPerformances() {
        val favouritesSet = _favourites.value ?: emptySet()
        val showFavourites = _showFavourites.value ?: false

        val updatedPerformances = _allPerformances.value?.map { performance ->
            performance.copy(favourite = favouritesSet.contains(performance.id))
        }

        _performances.value = if (showFavourites) {
            updatedPerformances?.filter { it.favourite }
        } else {
            updatedPerformances
        }
    }

    fun filterPerformancesByDay(day: String) {
        val showFavourites = _showFavourites.value ?: false
        val favouritesSet = _favourites.value ?: emptySet()

        _performances.value = _allPerformances.value?.filter {
            val dayMatches = it.day?.equals(day, ignoreCase = true) ?: false
            val isFavourite = it.id?.let { id -> favouritesSet.contains(id) } ?: false
            if (showFavourites) dayMatches && isFavourite else dayMatches
        }
    }

    fun addFavourite(performance: PerformanceModel) {
        val performanceId = performance.id
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (performanceId != null && userId != null) {
            dbFavourites.child(userId).child(performanceId).setValue(true)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
    }

    fun removeFavourite(performance: PerformanceModel) {
        val performanceId = performance.id
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (performanceId != null && userId != null) {
            dbFavourites.child(userId).child(performanceId).removeValue()
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
    }

    fun refreshFavourites() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            dbFavourites.child(userId).addListenerForSingleValueEvent(favouritesEventListener)
        }
    }

    override fun onCleared() {
        dbPerformances.removeEventListener(performanceEventListener)
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            dbFavourites.child(userId).removeEventListener(favouritesEventListener)
        }
    }
}