package com.sk.skimageslider

data class SliderItem(
    val imageUri: Any?, // Can be Int (drawable), String (URL/path), Uri, File
    val title: String? = null,
    val description: String? = null
)