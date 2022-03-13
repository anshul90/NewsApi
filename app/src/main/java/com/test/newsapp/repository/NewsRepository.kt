package com.test.newsapp.repository

import com.test.newsapp.api.RetrofitInstance
import com.test.newsapp.util.NetworkConnectionInterceptor

class NewsRepository(
    private val networkConnectionInterceptor: NetworkConnectionInterceptor
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.invoke(networkConnectionInterceptor)
            .getBreakingNews(countryCode, pageNumber)
}