package ie.wit.festifriend.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import ie.wit.festifriend.models.PerformanceModel

class ScheduleViewModel : ViewModel() {

    private val dbPerformances = FirebaseDatabase.getInstance().getReference("performances")

    private val _allPerformances = MutableLiveData<List<PerformanceModel>>().apply { value = emptyList() }
    private val _performances = MutableLiveData<List<PerformanceModel>>()
    val performances: LiveData<List<PerformanceModel>> = _performances

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val performances = mutableListOf<PerformanceModel>()
            snapshot.children.forEach { daySnapshot ->
                val day = daySnapshot.key ?: return
                daySnapshot.children.forEach { performanceSnapshot ->
                    val performance = performanceSnapshot.getValue(PerformanceModel::class.java)
                    performance?.day = day
                    performances.add(performance ?: return@forEach)
                }
            }
            _allPerformances.value = performances

        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    init {
        loadPerformances()
    }

    private fun loadPerformances() {
        dbPerformances.addValueEventListener(valueEventListener)
    }

    fun filterPerformancesByDay(day: String) {
        _performances.value = _allPerformances.value?.filter { it.day.equals(day, ignoreCase = true) } ?: emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        dbPerformances.removeEventListener(valueEventListener)
    }
}
