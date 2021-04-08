package com.ssellu.instaclone.navigation.model

data class NotificationDto(
    var destinationUid: String? = null,
    var userId: String? = null,
    var uid: String? = null,
    var type: Int? = null,
    var message: String? = null,
    var timestamp : Long? = null
)