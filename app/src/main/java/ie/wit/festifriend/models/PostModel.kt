package ie.wit.festifriend.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostModel(
    var id: String? = "",
    var userId: String = "",
    var userName: String? = "",
    var text: String? = "",
    var imageUrl: String? = "",
    var timestamp: Long = System.currentTimeMillis(),
    var likes: Int = 0,
    var userActions: Map<String, Boolean> = emptyMap()
): Parcelable
