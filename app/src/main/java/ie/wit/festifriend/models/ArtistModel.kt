package ie.wit.festifriend.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtistModel(
    var id: String? = "",
    var name: String? = "",
    var imageUrl: String? = "",
    var bio: String? = ""
): Parcelable

