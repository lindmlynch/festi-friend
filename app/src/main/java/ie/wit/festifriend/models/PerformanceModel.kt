package ie.wit.festifriend.models

data class PerformanceModel(

    val id: String,
    val name: String,
    val description: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val location: String
)