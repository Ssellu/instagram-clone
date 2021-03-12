package com.ssellu.instaclone.navigation.model

data class ContentDto(
    var explain: String? = "",
    var imageUrl: String? = "",
    var uid: String? = "",
    var userId: String? = "",
    var timestamp: Long? = null,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = HashMap()
) {
    data class Comment(
        var uid: String? = "",
        var userId: String? = "",
        var comment: String? = "",
        var timestamp: Long? = null
    )
}