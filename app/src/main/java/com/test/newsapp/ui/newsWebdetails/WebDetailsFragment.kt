package com.test.newsapp.ui.newsWebdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.test.newsapp.R
import com.test.newsapp.databinding.FragmentWebDetailsBinding
import com.test.newsapp.extensions.applyAppBarLayout
import com.test.newsapp.extensions.applyDefaultScrollFlags
import com.test.newsapp.extensions.applySupportActionBar
import com.test.newsapp.models.Article

open class WebDetailsFragment : Fragment() {
    private var _binding: FragmentWebDetailsBinding? = null
    private val homeBtnEnabled = true

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val arg: WebDetailsFragmentArgs by navArgs()
    private lateinit var article: Article

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        _binding = FragmentWebDetailsBinding.inflate(inflater, container, false)
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

        article = arg.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }
        applySupportActionBar {
            title = article.title
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        }
    }

    open fun enableBackButton(finishOnBackPressed: Boolean = false) {
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
    //endregion
}