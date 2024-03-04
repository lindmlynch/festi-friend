package ie.wit.festifriend.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ie.wit.festifriend.models.PerformanceModel

class ScheduleViewModel : ViewModel() {

    private val dbPerformances = FirebaseDatabase.getInstance().getReference("performances")

    private val _allPerformances = MutableLiveData<List<PerformanceModel>>()
    val performances: LiveData<List<PerformanceModel>> = _allPerformances

    init {
        loadPerformances()
    }

    private fun loadPerformances() {
        dbPerformances.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val performances = mutableListOf<PerformanceModel>()
                snapshot.children.forEach { daySnapshot ->
                    val day = daySnapshot.key
                    daySnapshot.children.forEach { performanceSnapshot ->
                        val performance = performanceSnapshot.getValue(PerformanceModel::class.java)
                        performance?.day = day
                        performance?.let { performances.add(it) }
                    }
                }
                _allPerformances.value = performances
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    fun filterPerformancesByDay(day: String) {
        _allPerformances.value?.let { allPerformances ->
            val filteredPerformances = allPerformances.filter { it.day.equals(day, ignoreCase = true) }
            _allPerformances.value = filteredPerformances
        }
    }
}
