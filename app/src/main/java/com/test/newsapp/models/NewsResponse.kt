package com.test.newsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
) : Parcelable