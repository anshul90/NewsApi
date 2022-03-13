package com.test.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.newsapp.R
import com.test.newsapp.databinding.FragmentHomeBinding
import com.test.newsapp.extensions.applyAppBarLayout
import com.test.newsapp.extensions.applyDefaultScrollFlags
import com.test.newsapp.extensions.applySupportActionBar
import com.test.newsapp.repository.NewsRepository
import com.test.newsapp.ui.ViewModelProviderFactory
import com.test.newsapp.util.NetworkConnectionInterceptor
import com.test.newsapp.util.Resource

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val homeBtnEnabled = true

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    lateinit var viewModel: HomeVM
    lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        enableBackButton()
        applyAppBarLayout {
            setExpanded(false)
        }
    }

    override fun onPause() {
        super.onPause()
        applyDefaultScrollFlags()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        homeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_detailsFragment,
                bundle
            )
        }

        getNews()
    }

    private fun getNews() {
        val networkConnectionInterceptor = activity?.let { NetworkConnectionInterceptor(it) }
        val newsRepository = networkConnectionInterceptor?.let { NewsRepository(it) }
        val viewModelProviderFactory = newsRepository?.let { ViewModelProviderFactory(it) }
        viewModel = viewModelProviderFactory?.let {
            ViewModelProvider(
                this,
                it
            )[HomeVM::class.java]
        }!!

        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        homeAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / 20 + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    hideRecyclerView()
                    response.message?.let { message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter()
        binding.recyclerView.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeFragment.scrollListener)
        }
    }

    private fun enableBackButton(finishOnBackPressed: Boolean = false) {
        applySupportActionBar {
            setHomeActionContentDescription(R.string.button_title_back)
            if (homeBtnEnabled) {
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
            if (finishOnBackPressed) {
                addBackDispatcher()
            }
        }
    }

    private fun addBackDispatcher() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    closeActivity()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    protected open fun closeActivity() {
        activity?.finish()
    }

    private fun hideRecyclerView() {
        if (binding.recyclerView.adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.internetImageView.isVisible = true
        } else {
            binding.recyclerView.isVisible = true
            binding.internetImageView.isVisible = false
        }
    }
}