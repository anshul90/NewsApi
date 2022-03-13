package com.test.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.newsapp.repository.NewsRepository
import com.test.newsapp.ui.home.HomeVM

class ViewModelProviderFactory(private val newsRepository: NewsRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeVM(newsRepository) as T
    }
}