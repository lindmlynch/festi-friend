package ie.wit.festifriend.models

data class PostModel(
    var userId: String? = "",
    var userName: String? = "",
    var text: String? = "",
    var imageUrl: String? = "",
    var timestamp: Long = System.currentTimeMillis()
)
