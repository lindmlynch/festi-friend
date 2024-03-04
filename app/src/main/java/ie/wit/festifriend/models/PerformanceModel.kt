package ie.wit.festifriend.models

import com.google.firebase.database.IgnoreExtraProperties
@IgnoreExtraProperties
data class PerformanceModel(

    var id: String = "",
    var name: String = "",
    var description: String = "",
    var day: String? = "",
    var startTime: String = "",
    var endTime: String = "",
    var location: String = ""
)

