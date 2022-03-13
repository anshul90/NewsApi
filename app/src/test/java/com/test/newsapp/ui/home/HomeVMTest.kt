package com.test.newsapp.ui.home

import android.content.Context
import android.os.Build.VERSION_CODES.Q
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.test.newsapp.models.Article
import com.test.newsapp.models.NewsResponse
import com.test.newsapp.models.Source
import com.test.newsapp.repository.NewsRepository
import com.test.newsapp.util.NetworkConnectionInterceptor
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Q])
class HomeVMTest : TestCase() {

    lateinit var viewModel: HomeVM
    lateinit var newsRepository: NewsRepository
    private var instrumentationContext: Context =
        InstrumentationRegistry.getInstrumentation().context

    @Mock
    var apiService: NetworkConnectionInterceptor =
        NetworkConnectionInterceptor(instrumentationContext)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        newsRepository = NewsRepository(apiService)
        viewModel = HomeVM(newsRepository)
    }

    @Test
    fun getBreakingNewsTest() {
        val country: String = "in"
        val pageNo: Int = 1
        runBlocking {
            Mockito.`when`(newsRepository.getBreakingNews(country, pageNo))
                .thenReturn(
                    Response.success(
                        NewsResponse(
                            mutableListOf<Article>(
                                Article(
                                    "Author Name", "Content", "Test description",
                                    "2022-03-13T08:18:00Z", Source("reuters", "Reuters"), "", "", ""
                                )
                            ), "ok", 1
                        )
                    )
                )
            viewModel.getBreakingNews(country)
            val result = viewModel.breakingNews
            assertEquals(
                listOf<Article>(Article("", "", "", "", Source("", ""), "", "", "")),
                result
            )
        }
    }


    @Test
    fun `empty list of news test`() {
        val country: String? = null
        val pageNo: Int = 1
        val articles: MutableList<Article>? = null
        runBlocking {
            Mockito.`when`(country?.let { newsRepository.getBreakingNews(it, pageNo) })
                .thenReturn(
                    Response.success(
                        NewsResponse(
                            mutableListOf<Article>(
                                Article(
                                    "", "", "", "", null, "", "", ""
                                )
                            ), "ok", 0
                        )
                    )
                )
            country?.let { viewModel.getBreakingNews(it) }
            val result = viewModel.breakingNews
            assertEquals(
                articles?.let { NewsResponse(it, "error", 0) },
                result
            )
        }
    }
}