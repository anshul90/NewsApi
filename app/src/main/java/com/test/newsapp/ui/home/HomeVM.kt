package com.test.newsapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.newsapp.models.NewsResponse
import com.test.newsapp.repository.NewsRepository
import com.test.newsapp.util.NoInternetException
import com.test.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeVM(
    private val newsRepository: NewsRepository
) : ViewModel() {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsRepsonse: NewsResponse? = null

    init {
        getBreakingNews(countryCode = "us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        try {
            breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is NoInternetException -> breakingNews.postValue(Resource.Error("Sorry, there is no internet connection"))
                else -> breakingNews.postValue(Resource.Error("Something went wrong"))
            }
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsRepsonse == null) {
                    breakingNewsRepsonse = resultResponse
                } else {
                    val oldArticles = breakingNewsRepsonse?.articles
                    val newArticle = resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsRepsonse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}