package com.test.newsapp.ui.newsDetail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.test.newsapp.R
import com.test.newsapp.databinding.FragmentDetailsBinding
import com.test.newsapp.extensions.applyAppBarLayout
import com.test.newsapp.extensions.applyDefaultScrollFlags
import com.test.newsapp.extensions.applySupportActionBar
import com.test.newsapp.models.Article
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class NewsDetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val homeBtnEnabled = true

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val arg: NewsDetailsFragmentArgs by navArgs()
    private lateinit var article: Article

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        article = arg.article
        Glide.with(this).load(article.urlToImage).placeholder(R.drawable.no_image)
            .error(R.drawable.no_image).into(binding.imageView)
        binding.tvSource.text = "Sourced by: " + article.source?.name
        binding.tvTitle.text = article.title
        binding.tvDescription.text = article.content
        binding.tvTime.text = article.publishedAt?.let { parseDate(it) }

        applySupportActionBar {
            title = article.title
        }

        binding.moreTextView.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_webDetailsFragment,
                bundle
            )

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

    private fun parseDate(date: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
            formattedDate
        } else {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val formatter = SimpleDateFormat("dd MMM yyyy HH:mm")
            formatter.format(parser.parse(date))
        }
    }
    //endregion
}