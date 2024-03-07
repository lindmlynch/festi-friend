package ie.wit.festifriend.models

import com.google.firebase.database.IgnoreExtraProperties
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@IgnoreExtraProperties
@Parcelize
data class PerformanceModel(

    var id: String? = "",
    var name: String? = "",
    var description: String? = "",
    var day: String? = "",
    var startTime: String? = "",
    var endTime: String? = "",
    var location: String? = "",
    var artist: ArtistModel = ArtistModel()
): Parcelable

