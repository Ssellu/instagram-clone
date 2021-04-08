package com.ssellu.instaclone.navigation.model

// TODO 1
data class FollowDto(
    var followers : MutableMap<String, Boolean> = HashMap(),

    var followings : MutableMap<String, Boolean> = HashMap()
)