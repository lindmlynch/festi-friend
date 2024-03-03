package ie.wit.festifriend.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.festifriend.models.PerformanceModel

class ScheduleViewModel : ViewModel() {

    private val _allPerformances = MutableLiveData<List<PerformanceModel>>()
    private val _performances = MutableLiveData<List<PerformanceModel>>()
    val performances: LiveData<List<PerformanceModel>> = _performances

    init {
        getPerformances()
    }

    private fun getPerformances() {
        val sampleData = listOf(
            PerformanceModel("1", "Band A", "Rock Band", "Friday", "18:00", "19:00", "Main Stage"),
            PerformanceModel("2", "DJ B", "Electronic Music", "Saturday", "22:00", "23:30", "Dance Tent"),
            PerformanceModel("3", "Band C", "Pop Artist", "Sunday", "18:00", "19:00", "Main Stage")
        )
        _allPerformances.value = sampleData
        _performances.value = sampleData
    }

    fun filterPerformancesByDay(day: String) {
        _allPerformances.value?.let { allPerformances ->
            val filteredPerformances = allPerformances.filter { it.day.equals(day, ignoreCase = true) }
            _performances.value = filteredPerformances
        } ?: run {

            _performances.value = emptyList()
        }
    }
}
